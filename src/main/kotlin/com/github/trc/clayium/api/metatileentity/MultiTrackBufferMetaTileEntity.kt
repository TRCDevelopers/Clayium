package com.github.trc.clayium.api.metatileentity


import com.cleanroommc.modularui.factory.PosGuiData
import com.cleanroommc.modularui.screen.ModularPanel
import com.cleanroommc.modularui.utils.Alignment
import com.cleanroommc.modularui.value.sync.GuiSyncManager
import com.cleanroommc.modularui.value.sync.SyncHandlers
import com.cleanroommc.modularui.widgets.ItemSlot
import com.cleanroommc.modularui.widgets.SlotGroupWidget
import com.cleanroommc.modularui.widgets.layout.Column
import com.cleanroommc.modularui.widgets.layout.Row
import com.github.trc.clayium.api.capability.ClayiumCapabilities
import com.github.trc.clayium.api.capability.IPipeConnectionLogic
import com.github.trc.clayium.api.capability.impl.ClayiumItemStackHandler
import com.github.trc.clayium.api.capability.impl.FilteredItemHandler
import com.github.trc.clayium.api.capability.impl.ItemHandlerProxy
import com.github.trc.clayium.api.metatileentity.trait.AutoIoHandler
import com.github.trc.clayium.api.util.CUtils
import com.github.trc.clayium.api.util.ITier
import com.github.trc.clayium.api.util.MachineIoMode
import com.github.trc.clayium.api.util.MachineIoMode.M_1
import com.github.trc.clayium.api.util.MachineIoMode.M_2
import com.github.trc.clayium.api.util.MachineIoMode.M_3
import com.github.trc.clayium.api.util.MachineIoMode.M_4
import com.github.trc.clayium.api.util.MachineIoMode.M_5
import com.github.trc.clayium.api.util.MachineIoMode.M_6
import com.github.trc.clayium.api.util.MachineIoMode.M_ALL
import com.github.trc.clayium.api.util.MachineIoMode.NONE
import com.github.trc.clayium.common.gui.ClayGuiTextures
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.EnumFacing
import net.minecraft.util.ResourceLocation
import net.minecraftforge.common.capabilities.Capability
import net.minecraftforge.items.CapabilityItemHandler
import net.minecraftforge.items.IItemHandler
import net.minecraftforge.items.IItemHandlerModifiable
import net.minecraftforge.items.wrapper.CombinedInvWrapper
import kotlin.Int
import kotlin.math.max
import kotlin.math.min

class MultiTrackBufferMetaTileEntity(
    metaTileEntityId: ResourceLocation,
    tier: ITier,
) : MetaTileEntity(metaTileEntityId, tier,
    validInputModes = mBufferValidIoModes[getTrackRows(tier.numeric)-2], validOutputModes = mBufferValidIoModes[getTrackRows(tier.numeric)-2],
    "multi_track_buffer") {

    override val hasFrontFacing: Boolean = true
    override val pipeConnectionLogic: IPipeConnectionLogic = IPipeConnectionLogic.ItemPipe

    val trackRow = getTrackRows(tier.numeric)

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
    private val slotFilters = (0..<filtersHandler.slots).map { slot ->
        { stack: ItemStack ->
            val filterStack = filtersHandler.getStackInSlot(slot)
            val filter = filterStack.getCapability(ClayiumCapabilities.ITEM_FILTER, null)
            if (filterStack.isEmpty) true
            else if (filter != null) filter.test(stack)
            else filterStack.isItemEqual(stack)
        }
    }
    override val itemInventory = CombinedInvWrapper(*tracks)
    override val importItems: IItemHandlerModifiable = itemInventory
    override val exportItems: IItemHandlerModifiable = itemInventory
    val autoIoHandler: AutoIoHandler = MultiTrackIoHandler()

    override fun createMetaTileEntity(): MetaTileEntity {
        return MultiTrackBufferMetaTileEntity(this.metaTileEntityId, this.tier)
    }

    override fun onPlacement() {
        this.setInput(frontFacing.opposite, M_ALL)
        super.onPlacement()
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
            M_1 -> createFilteredItemHandler(getTrackWithFilter(0), facing)
            M_2 -> createFilteredItemHandler(getTrackWithFilter(1), facing)
            M_3 -> createFilteredItemHandler(getTrackWithFilter(2), facing)
            M_4 -> createFilteredItemHandler(getTrackWithFilter(3), facing)
            M_5 -> createFilteredItemHandler(getTrackWithFilter(4), facing)
            M_6 -> createFilteredItemHandler(getTrackWithFilter(5), facing)
            else -> createFilteredItemHandler(itemInventory, facing)
        }
    }

    private fun getTrackWithFilter(track: Int): IItemHandler = FilteredItemHandler(tracks[track], slotFilters[track])

    override fun buildUI(data: PosGuiData, syncManager: GuiSyncManager): ModularPanel {
        (0..<trackRow).forEach { syncManager.registerSlotGroup("mt_buffer_inv_${it}", 1) }
        val slotsRowString = "I".repeat(trackInvSize)
        return ModularPanel("multi_track_buffer")
            .flex {
                it.size(max(176, trackInvSize * 18 + 4 + 18 + /* margin*/ 12), 18 + trackRow * 18 + 94 + 2)
                it.align(Alignment.Center)
            }
            .columnWithPlayerInv {
                child(buildMainParentWidget(syncManager)
                    .child(Column().width(trackInvSize * 18 + 4 + 18).height(trackRow * 18)
                        .align(Alignment.Center)
                        .also { column ->
                            for ((i, handler) in tracks.withIndex()) {
                                column.child(Row().width(trackInvSize * 18 + 4 + 18).height(18)
                                    .child(SlotGroupWidget.builder()
                                        .matrix(slotsRowString)
                                        .key('I') { slotIndex ->
                                            ItemSlot().slot(SyncHandlers.itemSlot(handler, slotIndex)
                                                .slotGroup("mt_buffer_inv_${i}")
                                                .filter(slotFilters[i]))
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
            }
    }

    override fun writeToNBT(data: NBTTagCompound) {
        super.writeToNBT(data)
        CUtils.writeItems(filtersHandler, "filterSlots", data)
    }

    override fun readFromNBT(data: NBTTagCompound) {
        super.readFromNBT(data)
        CUtils.readItems(filtersHandler, "filterSlots", data)
    }

    private inner class MultiTrackIoHandler : AutoIoHandler.Combined(this@MultiTrackBufferMetaTileEntity, isBuffer = true) {
        override fun importFromNeighbors() {
            var remaining = amountPerAction
            for (side in EnumFacing.entries) {
                if (!(remaining > 0 && isImporting(side))) continue

                val neighbor = getNeighbor(side) ?: continue
                if (neighbor is MetaTileEntityHolder && neighbor.metaTileEntity is MultiTrackBufferMetaTileEntity) {
                    val neighborBuffer = neighbor.metaTileEntity as MultiTrackBufferMetaTileEntity
                    remaining = transferMultiTrack(neighborBuffer, this@MultiTrackBufferMetaTileEntity, remaining)
                } else {
                    remaining = transferItemStack(
                        from = neighbor.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, side.opposite) ?: continue,
                        to = getImportItems(side) ?: continue,
                        amount = remaining,
                    )
                }
            }
        }

        override fun exportToNeighbors() {
            var remaining = amountPerAction
            for (side in EnumFacing.entries) {
                if (!(remaining > 0 && isExporting(side))) continue

                val neighbor = getNeighbor(side) ?: continue
                if (neighbor is MetaTileEntityHolder && neighbor.metaTileEntity is MultiTrackBufferMetaTileEntity) {
                    val neighborBuffer = neighbor.metaTileEntity as MultiTrackBufferMetaTileEntity
                    remaining = transferMultiTrack(this@MultiTrackBufferMetaTileEntity, neighborBuffer, remaining)
                } else {
                    remaining = transferItemStack(
                        from = getExportItems(side) ?: continue,
                        to = neighbor.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, side.opposite) ?: continue,
                        amount = remaining,
                    )
                }
            }
        }

        /**
         * returns remaining amount
         */
        private fun transferMultiTrack(
            from: MultiTrackBufferMetaTileEntity,
            to: MultiTrackBufferMetaTileEntity,
            maxAmount: Int
        ): Int {
            var remain = maxAmount
            for (i in 0..<min(from.trackRow, to.trackRow)) {
                val fromHandler = from.tracks[i]
                val toHandler = to.tracks[i]
                remain = transferItemStack(from = fromHandler, to = toHandler, amount = remain)
            }
            return remain
        }
    }

    companion object {
        val mBufferValidIoModes = listOf(
            listOf(NONE, M_ALL, M_1, M_2),
            listOf(NONE, M_ALL, M_1, M_2, M_3),
            listOf(NONE, M_ALL, M_1, M_2, M_3, M_4),
            listOf(NONE, M_ALL, M_1, M_2, M_3, M_4, M_5),
            listOf(NONE, M_ALL, M_1, M_2, M_3, M_4, M_5, M_6)
        )

        fun getTrackRows(tier: Int) = when (tier) {
            in 4..8 -> tier - 2
            in 9..13 -> 6
            else -> 2
        }
    }
}