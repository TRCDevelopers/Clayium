package com.github.trc.clayium.common.metatileentities

import com.cleanroommc.modularui.api.drawable.IKey
import com.cleanroommc.modularui.drawable.DynamicDrawable
import com.cleanroommc.modularui.drawable.GuiTextures
import com.cleanroommc.modularui.drawable.ItemDrawable
import com.cleanroommc.modularui.factory.PosGuiData
import com.cleanroommc.modularui.screen.ModularPanel
import com.cleanroommc.modularui.utils.Alignment
import com.cleanroommc.modularui.value.sync.GuiSyncManager
import com.cleanroommc.modularui.value.sync.SyncHandlers
import com.cleanroommc.modularui.widget.ParentWidget
import com.cleanroommc.modularui.widgets.ItemSlot
import com.cleanroommc.modularui.widgets.PageButton
import com.cleanroommc.modularui.widgets.PagedWidget
import com.cleanroommc.modularui.widgets.SlotGroupWidget
import com.cleanroommc.modularui.widgets.layout.Grid
import com.cleanroommc.modularui.widgets.layout.Row
import com.cleanroommc.modularui.widgets.slot.ModularSlot
import com.github.trc.clayium.api.CValues
import com.github.trc.clayium.api.ClayEnergy
import com.github.trc.clayium.api.ClayiumApi
import com.github.trc.clayium.api.block.ItemBlockMachine
import com.github.trc.clayium.api.capability.ClayiumTileCapabilities
import com.github.trc.clayium.api.capability.impl.EmptyItemStackHandler
import com.github.trc.clayium.api.capability.impl.ListeningItemStackHandler
import com.github.trc.clayium.api.laser.ClayLaser
import com.github.trc.clayium.api.metatileentity.ClayLaserMetaTileEntity
import com.github.trc.clayium.api.metatileentity.MetaTileEntity
import com.github.trc.clayium.api.pan.IPanAdapter
import com.github.trc.clayium.api.pan.IPanCable
import com.github.trc.clayium.api.pan.IPanRecipe
import com.github.trc.clayium.api.util.CUtils
import com.github.trc.clayium.api.util.ITier
import com.github.trc.clayium.api.util.clayiumId
import com.github.trc.clayium.api.util.toList
import com.github.trc.clayium.client.model.ModelTextures
import com.github.trc.clayium.common.gui.ClayGuiTextures
import com.google.common.collect.ImmutableSet
import net.minecraft.block.state.IBlockState
import net.minecraft.client.renderer.block.model.BakedQuad
import net.minecraft.client.renderer.block.model.FaceBakery
import net.minecraft.client.renderer.texture.TextureAtlasSprite
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.EnumFacing
import net.minecraft.util.ResourceLocation
import net.minecraftforge.common.capabilities.Capability
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import net.minecraftforge.items.IItemHandlerModifiable
import net.minecraftforge.items.ItemStackHandler
import java.util.function.Function

class PanAdapterMetaTileEntity(
    metaTileEntityId: ResourceLocation,
    tier: ITier,
) : MetaTileEntity(metaTileEntityId, tier, onlyNoneList, onlyNoneList,
    "machine.${CValues.MOD_ID}.pan_adapter.${tier.lowerName}"), IPanAdapter {

    override val requiredTextures get() = listOf(clayiumId("blocks/pan_adapter"))

    override val importItems = EmptyItemStackHandler
    override val exportItems = EmptyItemStackHandler
    override val itemInventory = EmptyItemStackHandler

    private val pageNum = when (tier.numeric) {
        10 -> 1
        11 -> 2
        12 -> 4
        13 -> 8
        else -> 1
    }

    private val recipeInventories = List(pageNum) { ListeningItemStackHandler(9, ::onSlotChanged) }
    private val resultInventories = List(pageNum) { ItemStackHandler(9) }
    private val laserInventory = ListeningItemStackHandler(9, ::onSlotChanged)
    private val currentEntries = mutableSetOf<IPanRecipe>()

    private fun onSlotChanged(slot: Int) {
        markDirty()
        refreshEntries()
    }

    /**
     * @return LaserEnergy, EnergyCost/t
     */
    private fun calculateLaserEnergy(): Pair<Double, ClayEnergy> {
        val laserRgb = IntArray(3)
        var energyCost = ClayEnergy.ZERO
        for (i in 0..<laserInventory.slots) {
            val stack = laserInventory.getStackInSlot(i)
            if (stack.item !is ItemBlockMachine) continue
            val laserMte = (CUtils.getMetaTileEntity(stack) as? ClayLaserMetaTileEntity)  ?: continue
            val laser = laserMte.laserManager.laser
            val laserCostPerTick = laserMte.energyCost
            (0..<stack.count).forEach {
                laserRgb[0] += laser.red
                laserRgb[1] += laser.green
                laserRgb[2] += laser.blue
                energyCost += laserCostPerTick
            }
        }
        return Pair(ClayLaser(EnumFacing.NORTH, laserRgb[0], laserRgb[1], laserRgb[2]).energy, energyCost)
    }

    override fun createMetaTileEntity(): MetaTileEntity {
        return PanAdapterMetaTileEntity(metaTileEntityId, tier)
    }

    override fun <T> getCapability(capability: Capability<T>, facing: EnumFacing?): T? {
        return when {
            capability === ClayiumTileCapabilities.PAN_CABLE -> capability.cast(IPanCable.INSTANCE)
            capability === ClayiumTileCapabilities.PAN_ADAPTER -> capability.cast(this)
            else -> super.getCapability(capability, facing)
        }
    }

    override fun getEntries(): Set<IPanRecipe> {
        return ImmutableSet.copyOf(currentEntries)
    }

    private fun refreshEntries() {
        val world = world ?: return
        val pos = pos ?: return
        val (laserEnergy, cet) = calculateLaserEnergy()
        currentEntries.clear()
        for ((pattern, result) in recipeInventories.zip(resultInventories)) {
            val stacks = pattern.toList()
            var entry: IPanRecipe? = null
            for (side in EnumFacing.entries) {
                entry = ClayiumApi.PAN_RECIPE_FACTORIES.firstNotNullOfOrNull { factory ->
                    factory.getEntry(world, pos.offset(side), stacks, laserEnergy, cet)
                }
                if (entry != null) break
            }
            if (entry == null) {
                resetResult(result)
            } else {
                currentEntries.add(entry)
                setResult(result, entry)
            }
        }
    }

    private fun setResult(resultHandler: IItemHandlerModifiable, entry: IPanRecipe) {
        val stacks = entry.results
        for (i in 0..<resultHandler.slots) {
            resultHandler.setStackInSlot(i, stacks.getOrNull(i) ?: break)
        }
    }

    private fun resetResult(resultHandler: IItemHandlerModifiable) {
        for (i in 0..<resultHandler.slots) {
            resultHandler.setStackInSlot(i, ItemStack.EMPTY)
        }
    }

    override fun onNeighborChanged(facing: EnumFacing) {
        super.onNeighborChanged(facing)
        refreshEntries()
    }

    override fun onRemoval() {
        super.onRemoval()
    }

    override fun writeToNBT(data: NBTTagCompound) {
        super.writeToNBT(data)
        recipeInventories.forEachIndexed { i, h ->
            CUtils.writeItems(h, "panAdapterPattern$i", data)
        }
    }

    override fun readFromNBT(data: NBTTagCompound) {
        super.readFromNBT(data)
        recipeInventories.forEachIndexed { i, h ->
            CUtils.readItems(h, "panAdapterPattern$i", data)
        }
    }

    override fun onFirstTick() {
        super.onFirstTick()
        this.refreshEntries()
    }

    override fun buildUI(data: PosGuiData, syncManager: GuiSyncManager): ModularPanel {
        val tabController = PagedWidget.Controller()
        val buttons = Grid.mapToMatrix(2, resultInventories) { index, handler ->
            ParentWidget().size(16)
                .child(PageButton(index, tabController)
                    .background(false, GuiTextures.MC_BUTTON)
                    .background(true, ClayGuiTextures.BUTTON_PRESSED)
                    .disableHoverBackground()
                    .size(16, 16))
                .child(DynamicDrawable { ItemDrawable(handler.getStackInSlot(0)) }.asWidget().size(16)
                    .tooltip { it.addLine(IKey.dynamic { if (handler.getStackInSlot(0).isEmpty) "<no recipe>" else handler.getStackInSlot(0).displayName }) }
                )
        }
        val pages = recipeInventories.zip(resultInventories).map{ (pattern, result) ->
            val slots = SlotGroupWidget.builder()
                .matrix("III", "III", "III")
                .key('I') { ItemSlot().slot(SyncHandlers.phantomItemSlot(pattern, it))
                    .background(ClayGuiTextures.FILTER_SLOT) }
                .build()
            val resultSlots = SlotGroupWidget.builder()
                .matrix("III", "III", "III")
                .key('I') { ItemSlot().slot(SyncHandlers.itemSlot(result, it).accessibility(false, false)) }
                .build()
            Row().widthRel(1f).height(64)
                .child(Grid().width(32).heightRel(1f).align(Alignment.TopLeft)
                    .minElementMargin(0, 0)
                    .matrix(buttons)
                )
                .child(slots.left(32 + 8))
                .child(resultSlots.align(Alignment.TopRight))
        }
        return ModularPanel.defaultPanel("pan_adapter", 176, 196)
            .child(mainColumn {
                child(buildMainParentWidget(syncManager)
                    .child(PagedWidget().margin(0, 9).widthRel(1f).height(16*4)
                        .controller(tabController)
                        .apply { for (page in pages) addPage(page) }
                    )
                    .child(SlotGroupWidget.builder()
                        .row("I".repeat(9))
                        .key('I') { index ->
                            ItemSlot().slot(ModularSlot(laserInventory, index))
                                .tooltip { it.addLine(IKey.lang("machine.clayium.pan_adapter.laser_slot_tooltip")) }
                        }
                        .build()
                        .bottom(10)
                    )
                )
            })
    }

    @SideOnly(Side.CLIENT)
    override fun registerItemModel(item: Item, meta: Int) {
        registerItemModelDefault(item, meta, "pan_adapter")
    }

    @SideOnly(Side.CLIENT)
    override fun bakeQuads(getter: Function<ResourceLocation, TextureAtlasSprite>, faceBakery: FaceBakery) {
        val sprite = getter.apply(clayiumId("blocks/pan_adapter"))
        adapterQuads = EnumFacing.entries.map {
            ModelTextures.createQuad(it, sprite)
        }
    }

    override fun overlayQuads(quads: MutableList<BakedQuad>, state: IBlockState?, side: EnumFacing?, rand: Long) {
        if (state == null || side == null) return
        quads.add(adapterQuads[side.index])
    }

    companion object {
        private lateinit var adapterQuads: List<BakedQuad>
    }
}