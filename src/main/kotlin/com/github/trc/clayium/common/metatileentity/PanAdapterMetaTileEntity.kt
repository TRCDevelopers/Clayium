package com.github.trc.clayium.common.metatileentity

import com.cleanroommc.modularui.api.drawable.IKey
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
import com.cleanroommc.modularui.widgets.layout.Column
import com.cleanroommc.modularui.widgets.layout.Row
import com.cleanroommc.modularui.widgets.slot.ModularSlot
import com.github.trc.clayium.api.CValues
import com.github.trc.clayium.api.ClayiumApi
import com.github.trc.clayium.api.capability.ClayiumTileCapabilities
import com.github.trc.clayium.api.capability.impl.EmptyItemStackHandler
import com.github.trc.clayium.api.capability.impl.ListeningItemStackHandler
import com.github.trc.clayium.api.metatileentity.MetaTileEntity
import com.github.trc.clayium.api.pan.IPanAdapter
import com.github.trc.clayium.api.pan.IPanCable
import com.github.trc.clayium.api.pan.IPanEntry
import com.github.trc.clayium.api.pan.IPanNotifiable
import com.github.trc.clayium.api.pan.isPanCable
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
import net.minecraft.init.Items
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.util.EnumFacing
import net.minecraft.util.ResourceLocation
import net.minecraftforge.common.capabilities.Capability
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import net.minecraftforge.items.IItemHandler
import net.minecraftforge.items.IItemHandlerModifiable
import net.minecraftforge.items.ItemStackHandler
import java.util.function.Function

class PanAdapterMetaTileEntity(
    metaTileEntityId: ResourceLocation,
    tier: ITier,
) : MetaTileEntity(metaTileEntityId, tier, onlyNoneList, onlyNoneList, "${CValues.MOD_ID}.machine.pan_adapter"), IPanAdapter {

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
    private val currentEntries = mutableSetOf<IPanEntry>()

    private var network: IPanNotifiable? = null

    private fun onSlotChanged(slot: Int) {
        refreshEntries()
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

    override fun getEntries(): Set<IPanEntry> {
        return ImmutableSet.copyOf(currentEntries)
    }

    private fun refreshEntries() {
        val world = world ?: return
        val pos = pos ?: return
        currentEntries.clear()
        for ((pattern, result) in recipeInventories.zip(resultInventories)) {
            val stacks = pattern.toList()
            var entry: IPanEntry? = null
            for (side in EnumFacing.entries) {
                entry = ClayiumApi.PAN_ENTRY_FACTORIES.firstNotNullOfOrNull { factory ->
                    factory.getEntry(world, pos.offset(side), stacks)
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

    private fun setResult(resultHandler: IItemHandlerModifiable, entry: IPanEntry) {
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
        network?.notifyNetwork()
    }

    override fun setCore(network: IPanNotifiable) {
        this.network = network
    }

    override fun coreRemoved() {
        network = null
    }

    override fun buildUI(data: PosGuiData, syncManager: GuiSyncManager): ModularPanel {
        val tabController = PagedWidget.Controller()
        val pages = recipeInventories.zip(resultInventories).map { (pattern, result) ->
            val slots = SlotGroupWidget.builder()
                .matrix("III", "III", "III")
                .key('I') { ItemSlot().slot(SyncHandlers.phantomItemSlot(pattern, it))
                    .background(ClayGuiTextures.FILTER_SLOT) }
                .build()
            val resultSlots = SlotGroupWidget.builder()
                .matrix("III", "III", "III")
                .key('I') { ItemSlot().slot(SyncHandlers.itemSlot(result, it).accessibility(false, false)) }
                .build()
            Row().sizeRel(1f)
                .child(slots.align(Alignment.TopLeft))
                .child(resultSlots.align(Alignment.TopRight))
        }
        return ModularPanel.defaultPanel("pan_adapter", 176, 196)
            .childIf(this.pageNum < 8, Row().coverChildren().topRel(0f, 4, 1f)
                .apply {
                    for (i in 0..<pageNum) {
                        child(ParentWidget().coverChildren()
                            .child(PageButton(i, tabController)
                                .tab(GuiTextures.TAB_TOP, if (i == 0) -1 else 0))
                            .child(ItemDrawable(ItemStack(Items.APPLE)).asWidget()
                                .size(16, 16)
                                .align(Alignment.Center))
                        )
                    }
                }
            )
            .childIf(this.pageNum == 8, Column().coverChildren().leftRel(0f, 4, 1f)
                .apply {
                    for (i in 0..<4) {
                        child(ParentWidget().coverChildren()
                            .child(PageButton(i, tabController)
                                .tab(GuiTextures.TAB_LEFT, if (i == 0) -1 else 0))
                            .child(ItemDrawable(ItemStack(Items.APPLE)).asWidget()
                                .size(16, 16)
                                .align(Alignment.Center))
                        )
                    }
                }
            )
            .childIf(this.pageNum == 8, Column().coverChildren().rightRel(0f, 4, 1f)
                .apply {
                    for (i in 4..<8) {
                        child(ParentWidget().coverChildren()
                            .child(PageButton(i, tabController)
                                .tab(GuiTextures.TAB_RIGHT, if (i == 4) -1 else 0))
                            .child(ItemDrawable(ItemStack(Items.APPLE)).asWidget()
                                .size(16, 16)
                                .align(Alignment.Center))
                        )
                    }
                }
            )
            .child(Column().margin(7).sizeRel(1f)
                .child(ParentWidget().widthRel(1f).expanded().marginBottom(2)
                    .child(IKey.lang(this.translationKey, IKey.lang(tier.prefixTranslationKey)).asWidget()
                        .align(Alignment.TopLeft))
                    .child(IKey.lang("container.inventory").asWidget()
                        .align(Alignment.BottomLeft))
                    .child(PagedWidget().margin(0, 9).sizeRel(1f)
                        .controller(tabController)
                        .apply { for (page in pages) addPage(page
                            .child(SlotGroupWidget.builder()
                                .row("I".repeat(9))
                                .key('I') { index -> ItemSlot().slot(ModularSlot(laserInventory, index)) }
                                .build()
                                .align(Alignment.BottomCenter).marginBottom(8))) }
                    )
                )
                .child(SlotGroupWidget.playerInventory(0))
            )
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