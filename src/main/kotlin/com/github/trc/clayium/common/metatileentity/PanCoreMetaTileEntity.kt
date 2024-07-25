package com.github.trc.clayium.common.metatileentity

import com.cleanroommc.modularui.api.drawable.IKey
import com.cleanroommc.modularui.drawable.ItemDrawable
import com.cleanroommc.modularui.drawable.Rectangle
import com.cleanroommc.modularui.factory.PosGuiData
import com.cleanroommc.modularui.screen.ModularPanel
import com.cleanroommc.modularui.utils.Alignment
import com.cleanroommc.modularui.utils.Color
import com.cleanroommc.modularui.value.sync.GuiSyncManager
import com.cleanroommc.modularui.widget.ParentWidget
import com.cleanroommc.modularui.widget.scroll.VerticalScrollData
import com.cleanroommc.modularui.widgets.SlotGroupWidget
import com.cleanroommc.modularui.widgets.layout.Column
import com.cleanroommc.modularui.widgets.layout.Grid
import com.github.trc.clayium.api.CValues
import com.github.trc.clayium.api.capability.ClayiumDataCodecs.UPDATE_PAN_DUPLICATION_ENTRIES
import com.github.trc.clayium.api.capability.ClayiumTileCapabilities
import com.github.trc.clayium.api.capability.impl.EmptyItemStackHandler
import com.github.trc.clayium.api.metatileentity.MetaTileEntity
import com.github.trc.clayium.api.pan.IPan
import com.github.trc.clayium.api.pan.IPanCable
import com.github.trc.clayium.api.pan.IPanUser
import com.github.trc.clayium.api.pan.IPanNotifiable
import com.github.trc.clayium.api.pan.IPanRecipe
import com.github.trc.clayium.api.pan.isPanCable
import com.github.trc.clayium.api.util.ITier
import com.github.trc.clayium.api.util.clayiumId
import com.github.trc.clayium.client.model.ModelTextures
import com.github.trc.clayium.common.Clayium
import com.github.trc.clayium.common.blocks.ClayiumBlocks
import com.github.trc.clayium.common.clayenergy.ClayEnergy
import com.github.trc.clayium.common.clayenergy.readClayEnergy
import com.github.trc.clayium.common.clayenergy.writeClayEnergy
import com.github.trc.clayium.common.config.ConfigCore
import com.github.trc.clayium.common.recipe.ingredient.CRecipeInput
import com.github.trc.clayium.common.unification.OreDictUnifier
import com.github.trc.clayium.common.unification.material.CMaterials
import com.github.trc.clayium.common.unification.material.PropertyKey
import com.github.trc.clayium.common.unification.ore.OrePrefix
import com.github.trc.clayium.common.unification.stack.ItemAndMeta
import com.github.trc.clayium.common.unification.stack.readItemAndMeta
import com.github.trc.clayium.common.unification.stack.writeItemAndMeta
import net.minecraft.block.state.IBlockState
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.block.model.BakedQuad
import net.minecraft.client.renderer.block.model.FaceBakery
import net.minecraft.client.renderer.block.model.ModelResourceLocation
import net.minecraft.client.renderer.texture.TextureAtlasSprite
import net.minecraft.client.util.ITooltipFlag
import net.minecraft.init.Blocks
import net.minecraft.item.Item
import net.minecraft.network.PacketBuffer
import net.minecraft.util.EnumFacing
import net.minecraft.util.ResourceLocation
import net.minecraft.util.math.BlockPos
import net.minecraftforge.client.model.ModelLoader
import net.minecraftforge.common.capabilities.Capability
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import java.util.function.Function

class PanCoreMetaTileEntity(
    metaTileEntityId: ResourceLocation,
    tier: ITier,
) : MetaTileEntity(metaTileEntityId, tier, onlyNoneList, onlyNoneList, "${CValues.MOD_ID}.pan_core"), IPanNotifiable, IPan {
    override val importItems = EmptyItemStackHandler
    override val exportItems = EmptyItemStackHandler
    override val itemInventory = EmptyItemStackHandler

    private val panRecipes = mutableSetOf<IPanRecipe>()
    private var networkNotified = false
    private val panNodes = mutableListOf<IPanUser>()

    private val duplicationEntries = mutableMapOf<ItemAndMeta, PanDuplicationEntry>()

    override fun getDuplicationEntries(): Map<ItemAndMeta, ClayEnergy> {
        return duplicationEntries.asSequence()
            .filter { (_, e) -> e.isAllowedToDuplicate }
            .map { (k, v) -> k to v.energy }
            .toMap()
    }

    override fun update() {
        super.update()
        onServer {
            if (offsetTimer % REFRESH_RATE_TICKS == 0L) {
                refreshNetworkAndThenEntries()
            }
        }
    }

    private fun refreshNetworkAndThenEntries() {
        this.panRecipes.clear()
        val pos = this.pos ?: return
        val nodes = mutableSetOf<BlockPos>()
        searchNodes(nodes, pos, 0)
        for (pos in nodes) {
            val tileEntity = world?.getTileEntity(pos) ?: continue
            val node = tileEntity.getCapability(ClayiumTileCapabilities.PAN_USER, null)
            if (node != null) {
                panNodes.add(node)
                node.setNetwork(this)
            }
            val panAdapter = tileEntity.getCapability(ClayiumTileCapabilities.PAN_ADAPTER, null)
            if (panAdapter != null) {
                this.panRecipes.addAll(panAdapter.getEntries())
            }
        }
        refreshDuplicationEntries()
    }

    private fun searchNodes(nodes: MutableSet<BlockPos>, pos: BlockPos, depth: Int) {
        if (depth > ConfigCore.misc.panMaxSearchDistance) return
        val world = this.world ?: return
        for (side in EnumFacing.entries) {
            val target = pos.offset(side)
            if (world.isPanCable(target) && target !in nodes) {
                nodes.add(target)
                searchNodes(nodes, target, depth + 1)
            }
        }
    }

    private fun refreshDuplicationEntries() {
        duplicationEntries.clear()
        duplicationEntries.putAll(defaultDuplicationEntries)

        // Result -> Recipes that requires that as an ingredient
        val internalRecipes = panRecipes.map { PanRecipeInternal(it) }
        val result2Dependants = mutableMapOf<ItemAndMeta, MutableList<PanRecipeInternal>>()
        defaultDuplicationEntries.keys.forEach { result2Dependants[it] = mutableListOf() }

        // add results that gathered from pan adapters as a key to the trees
        // keys (ItemAndMetas) that added here are duplicatable if tree roots are default duplication entries.
        for (recipe in panRecipes) {
            for (result in recipe.results) {
                val key = ItemAndMeta(result)
                result2Dependants.getOrPut(key, ::mutableListOf)
            }
        }
        // construct recipe/ingredient map
        for (recipeInternal in internalRecipes) {
            val recipe = recipeInternal.panRecipe
            for (ingredient in recipe.ingredients) {
                val keys = ingredient.stacks.map { ItemAndMeta(it) }
                keys.firstOrNull {
                    result2Dependants[it]?.apply { if (recipeInternal !in this) this.add(recipeInternal) } != null
                }
            }
        }

        val queue = ArrayDeque<ItemAndMeta>()
        val walked = mutableSetOf<ItemAndMeta>()
        queue.addAll(defaultDuplicationEntries.keys)
        while (queue.isNotEmpty()) {
            val parent = queue.removeFirst()
            if (parent in walked) {
                Clayium.LOGGER.warn("Tried to walk a node that has already been walked: $parent")
                continue
            }
            walked.add(parent)
            val childRecipes = result2Dependants[parent] ?: continue
            for (recipe in childRecipes) {
                for (ing in recipe.internalIngs) {
                    ing.verified = ing.verified || ing.ingredient.testIgnoringAmount(parent)
                }
                if (recipe.internalIngs.all { it.verified }) {
                    for (result in recipe.panRecipe.results.map(::ItemAndMeta)) {
                        queue.add(result)
                        //todo calculate cost
                        val cost = ClayEnergy(1)
                        duplicationEntries[result] = PanDuplicationEntry(cost)
                    }
                }
            }
        }

        writeCustomData(UPDATE_PAN_DUPLICATION_ENTRIES) {
            writeVarInt(duplicationEntries.size)
            for ((key, entry) in duplicationEntries) {
                writeItemAndMeta(key)
                writeClayEnergy(entry.energy)
                writeBoolean(entry.isAllowedToDuplicate)
            }
        }
    }

    override fun createMetaTileEntity(): MetaTileEntity {
        return PanCoreMetaTileEntity(metaTileEntityId, tier)
    }

    override fun onPlacement() {
        super.onPlacement()
        refreshNetworkAndThenEntries()
    }

    override fun onRemoval() {
        super.onRemoval()
        for (node in panNodes) {
            node.resetNetwork()
        }
    }

    override fun receiveCustomData(discriminator: Int, buf: PacketBuffer) {
        if (discriminator == UPDATE_PAN_DUPLICATION_ENTRIES) {
            duplicationEntries.clear()
            val entriesSize = buf.readVarInt()
            @Suppress("unused")
            for (unused in 0..<entriesSize) {
                val key = buf.readItemAndMeta()
                val energy = buf.readClayEnergy()
                val isAllowedToDuplicate = buf.readBoolean()
                duplicationEntries[key] = PanDuplicationEntry(energy, isAllowedToDuplicate)
            }
        }
        super.receiveCustomData(discriminator, buf)
    }

    override fun <T> getCapability(capability: Capability<T>, facing: EnumFacing?): T? {
        if (capability === ClayiumTileCapabilities.PAN_CABLE) {
            return capability.cast(IPanCable.INSTANCE)
        }
        return super.getCapability(capability, facing)
    }

    override fun notifyNetwork() {
        networkNotified = true
    }

    @SideOnly(Side.CLIENT)
    override fun registerItemModel(item: Item, meta: Int) {
        ModelLoader.setCustomModelResourceLocation(item, meta, ModelResourceLocation(clayiumId("pan_core"), "tier=${tier.lowerName}"))
    }

    @SideOnly(Side.CLIENT)
    override fun bakeQuads(getter: Function<ResourceLocation, TextureAtlasSprite>, faceBakery: FaceBakery) {
        val tex = getter.apply(clayiumId("blocks/pan_core"))
        panCoreQuads = EnumFacing.entries.map { ModelTextures.createQuad(it, tex) }.toMutableList()
    }

    @SideOnly(Side.CLIENT)
    override fun getQuads(quads: MutableList<BakedQuad>, state: IBlockState?, side: EnumFacing?, rand: Long) {
        if (state == null || side == null) return
        quads.add(panCoreQuads[side.index])
    }

    override fun buildUI(data: PosGuiData, syncManager: GuiSyncManager): ModularPanel {
        if (!isRemote) {
            refreshNetworkAndThenEntries()
        }
        val displayItems = Grid.mapToMatrix(8, duplicationEntries.toList()) { index, (itemAndMeta, entry) ->
            val stack = itemAndMeta.asStack()
            ItemDrawable(stack).asWidget().size(16)
                .tooltip { tooltip ->
                    if (isRemote) {
                        val flag = if (Minecraft.getMinecraft().gameSettings.advancedItemTooltips) ITooltipFlag.TooltipFlags.ADVANCED else ITooltipFlag.TooltipFlags.NORMAL
                        tooltip.addStringLines(stack.getTooltip(data.player, flag))
                    }
                    tooltip.addLine(entry.energy.format())
                }
                .also {
                    if (!entry.isAllowedToDuplicate) {
                        it.background(Rectangle().setColor(Color.RED.main))
                    }
                }
        }
        val panDisplayMargin = 4
        val panDisplayWidth = 18 * 8 + 4
        return ModularPanel.defaultPanel("pan_core", 176, 236)
            .child(Column().margin(7)
                .child(ParentWidget().widthRel(1f).expanded().marginBottom(2)
                    .child(IKey.lang(this.translationKey, IKey.lang(tier.prefixTranslationKey)).asWidget()
                        .align(Alignment.TopLeft))
                    .child(IKey.lang("container.inventory").asWidget()
                        .align(Alignment.BottomLeft))
                    .child(ParentWidget().width(panDisplayWidth + panDisplayMargin * 2).heightRel(1f)
                        .align(Alignment.TopCenter).margin(0, 2)
                        .child(Rectangle().setColor(Color.rgb(0, 0x1E, 0)).asWidget()
                            .width(panDisplayWidth + panDisplayMargin * 2).heightRel(1f).margin(0, 9))
                        .child(Grid().width(panDisplayWidth).heightRel(1f).margin(panDisplayMargin, 13)
                            .minElementMargin(0, 0)
                            .matrix(displayItems)
                            .scrollable(VerticalScrollData())
                            .background(Rectangle().setColor(Color.rgb(0, 0x1E, 0))))
                    )
                )
                .child(SlotGroupWidget.playerInventory(0)))
    }

    class PanDuplicationEntry(
        val energy: ClayEnergy,
        val isAllowedToDuplicate: Boolean = true,
    )
    private class PanIngredient(val ingredient: CRecipeInput, var verified: Boolean = false)
    private class PanRecipeInternal(val panRecipe: IPanRecipe) {
        val internalIngs = panRecipe.ingredients.map(::PanIngredient)
    }

    companion object {
        const val REFRESH_RATE_TICKS = 200
        private val defaultDuplicationEntries: Map<ItemAndMeta, PanDuplicationEntry> by lazy { mutableMapOf<ItemAndMeta, PanDuplicationEntry>().apply {
            put(ItemAndMeta(Blocks.COBBLESTONE), PanDuplicationEntry(ClayEnergy.micro(10)))
            put(ItemAndMeta(Blocks.LOG), PanDuplicationEntry(ClayEnergy.micro(10)))
            put(ItemAndMeta(Blocks.CLAY), PanDuplicationEntry(ClayEnergy.micro(10), false))
            ClayiumBlocks.COMPRESSED_CLAY_BLOCKS.forEach { block ->
                block.blockState.validStates.forEach { state ->
                    put(
                        ItemAndMeta(OreDictUnifier.get(OrePrefix.block, block.getCMaterial(state))),
                        PanDuplicationEntry(ClayEnergy.micro(10), false)
                    )
                }
            }
            ClayiumBlocks.ENERGIZED_CLAY_BLOCKS.forEach { block ->
                block.blockState.validStates.forEach { state ->
                    val material = block.getCMaterial(state)
                    val ce = material.getProperty(PropertyKey.CLAY).energy!!
                    put(
                        ItemAndMeta(OrePrefix.block, material),
                        PanDuplicationEntry(ce, false)
                    )
                }
            }
            put(ItemAndMeta(OrePrefix.gem, CMaterials.antimatter), PanDuplicationEntry(ClayEnergy.of(1), false))
        }.toMap() }

        private lateinit var panCoreQuads: MutableList<BakedQuad>
    }
}