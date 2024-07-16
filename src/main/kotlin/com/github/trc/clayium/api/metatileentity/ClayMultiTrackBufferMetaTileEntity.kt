package com.github.trc.clayium.api.metatileentity

import com.cleanroommc.modularui.api.drawable.IKey
import com.cleanroommc.modularui.factory.PosGuiData
import com.cleanroommc.modularui.screen.ModularPanel
import com.cleanroommc.modularui.utils.Alignment
import com.cleanroommc.modularui.value.sync.GuiSyncManager
import com.cleanroommc.modularui.value.sync.SyncHandlers
import com.cleanroommc.modularui.widget.ParentWidget
import com.cleanroommc.modularui.widgets.ItemSlot
import com.cleanroommc.modularui.widgets.SlotGroupWidget
import com.cleanroommc.modularui.widgets.layout.Column
import com.cleanroommc.modularui.widgets.layout.Row
import com.github.trc.clayium.api.CValues
import com.github.trc.clayium.api.capability.impl.ClayiumItemStackHandler
import com.github.trc.clayium.api.capability.impl.ItemHandlerProxy
import com.github.trc.clayium.api.util.ITier
import com.github.trc.clayium.api.util.clayiumId
import com.github.trc.clayium.common.blocks.machine.MachineIoMode
import com.github.trc.clayium.common.gui.ClayGuiTextures
import net.minecraft.client.renderer.block.model.ModelResourceLocation
import net.minecraft.item.Item
import net.minecraft.util.EnumFacing
import net.minecraft.util.ResourceLocation
import net.minecraftforge.client.model.ModelLoader
import net.minecraftforge.common.capabilities.Capability
import net.minecraftforge.items.CapabilityItemHandler
import net.minecraftforge.items.IItemHandler
import net.minecraftforge.items.IItemHandlerModifiable
import net.minecraftforge.items.wrapper.CombinedInvWrapper
import kotlin.Int
import kotlin.math.max

class ClayMultiTrackBufferMetaTileEntity(
    metaTileEntityId: ResourceLocation,
    tier: ITier,
) : MetaTileEntity(metaTileEntityId, tier,
    validInputModes = emptyList(), validOutputModes = emptyList(), "machine.${CValues.MOD_ID}.clay_multi_track_buffer") {

    override val hasFrontFacing: Boolean = true
    val trackRow = when (tier.numeric) {
        in 4..8 -> tier.numeric - 2
        in 9..13 -> 6
        else -> 2
    }

    override val validInputModes: List<MachineIoMode> = mBufferValidIoModes[trackRow-2]
    override val validOutputModes: List<MachineIoMode> = mBufferValidIoModes[trackRow-2]

    val trackInvSize = when (tier.numeric) {
        4 -> 1
        5 -> 2
        in 6..7 -> 4
        8 -> 6
        in 9..13 -> 9
        else -> 1
    }

    val tracks: Array<ClayiumItemStackHandler> = (0..<trackRow).map {
        ClayiumItemStackHandler(this, trackInvSize)
    }.toTypedArray()

    private val filtersHandler = ClayiumItemStackHandler(this, trackRow)
    override val itemInventory = CombinedInvWrapper(*tracks)
    override val importItems: IItemHandlerModifiable = itemInventory
    override val exportItems: IItemHandlerModifiable = itemInventory
    override val autoIoHandler: AutoIoHandler = AutoIoHandler.Combined(this, true)

    override fun createMetaTileEntity(): MetaTileEntity {
        validInputModes
        return ClayMultiTrackBufferMetaTileEntity(this.metaTileEntityId, this.tier)
    }

    override fun registerItemModel(item: Item, meta: Int) {
        ModelLoader.setCustomModelResourceLocation(item, meta, ModelResourceLocation(clayiumId("clay_multi_track_buffer"), "tier=${tier.numeric}"))
    }

    override fun <T> getCapability(capability: Capability<T>, facing: EnumFacing?): T? {
        if (capability === CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            if (facing == null) return CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.cast(itemInventory)

            return CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.cast(
                ItemHandlerProxy(
                    getItemHandler(getInput(facing), facing), getItemHandler(getOutput(facing), facing)
                )
            )
        }
        return super.getCapability(capability, facing)
    }

    private fun getItemHandler(mode: MachineIoMode, facing: EnumFacing): IItemHandler? {
        return when (mode) {
            MachineIoMode.M_1 -> createFilteredItemHandler(tracks[0], facing)
            MachineIoMode.M_2 -> createFilteredItemHandler(tracks[1], facing)
            MachineIoMode.M_3 -> createFilteredItemHandler(tracks[2], facing)
            MachineIoMode.M_4 -> createFilteredItemHandler(tracks[3], facing)
            MachineIoMode.M_5 -> createFilteredItemHandler(tracks[4], facing)
            MachineIoMode.M_6 -> createFilteredItemHandler(tracks[5], facing)
            else -> createFilteredItemHandler(itemInventory, facing)
        }
    }

    override fun buildUI(data: PosGuiData, syncManager: GuiSyncManager): ModularPanel? {
        (0..<trackRow).forEach { syncManager.registerSlotGroup("mt_buffer_inv_${it}", 1) }
        val slotsRowString = "I".repeat(trackInvSize)
        return ModularPanel("multi_track_buffer")
            .flex {
                it.size(max(176, trackInvSize * 18 + 4 + 18 + /* margin*/ 12), 18 + trackRow * 18 + 94 + 2)
                it.align(Alignment.Center)
            }
            .child(Column().margin(7)
                .child(ParentWidget().widthRel(1f).expanded().marginBottom(2)
                    .child(IKey.lang(this.translationKey, IKey.lang(tier.prefixTranslationKey)).asWidget()
                        .align(Alignment.TopLeft))
                    .child(IKey.lang("container.inventory").asWidget()
                        .align(Alignment.BottomLeft))
                    .child(Column().width(trackInvSize * 18 + 4 + 18).height(trackRow * 18)
                        .align(Alignment.Center)
                        .also {
                            for ((i, handler) in tracks.withIndex()) {
                                it.child(Row().width(trackInvSize * 18 + 4 + 18).height(18)
                                    .child(SlotGroupWidget.builder()
                                        .matrix(slotsRowString)
                                        .key('I') { slotIndex ->
                                            ItemSlot().slot(SyncHandlers.itemSlot(handler, slotIndex).slotGroup("mt_buffer_inv_${i}"))
                                                .background(ClayGuiTextures.M_TRACK_SLOTS[i])
                                        }
                                        .build())
                                    .child(ItemSlot().slot(SyncHandlers.phantomItemSlot(filtersHandler, i))
                                        .background(ClayGuiTextures.M_TRACK_FILTER_SLOTS[i])
                                        .align(Alignment.CenterRight)))
                            }
                        }
                    )
                )
                .child(SlotGroupWidget.playerInventory(0))
            )
    }
}