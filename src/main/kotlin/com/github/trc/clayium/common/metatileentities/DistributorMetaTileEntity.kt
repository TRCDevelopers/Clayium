package com.github.trc.clayium.common.metatileentities

import com.cleanroommc.modularui.screen.ModularPanel
import com.cleanroommc.modularui.utils.Alignment
import com.cleanroommc.modularui.value.sync.GuiSyncManager
import com.cleanroommc.modularui.value.sync.SyncHandlers
import com.cleanroommc.modularui.widget.ParentWidget
import com.cleanroommc.modularui.widgets.ItemSlot
import com.cleanroommc.modularui.widgets.SlotGroupWidget
import com.cleanroommc.modularui.widgets.layout.Column
import com.cleanroommc.modularui.widgets.layout.Row
import com.github.trc.clayium.api.GUI_DEFAULT_HEIGHT
import com.github.trc.clayium.api.GUI_DEFAULT_WIDTH
import com.github.trc.clayium.api.capability.impl.ClayiumItemStackHandler
import com.github.trc.clayium.api.gui.data.MetaTileEntityGuiData
import com.github.trc.clayium.api.metatileentity.MetaTileEntity
import com.github.trc.clayium.api.metatileentity.trait.AutoIoHandler
import com.github.trc.clayium.api.util.ITier
import com.github.trc.clayium.api.util.copyWithSize
import com.github.trc.clayium.api.util.enumMapNotNull
import com.github.trc.clayium.api.util.next
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.EnumFacing
import net.minecraft.util.ResourceLocation
import net.minecraftforge.common.capabilities.Capability
import net.minecraftforge.items.CapabilityItemHandler
import net.minecraftforge.items.ItemHandlerHelper
import net.minecraftforge.items.wrapper.CombinedInvWrapper
import kotlin.math.min

class DistributorMetaTileEntity(
    metaTileEntityId: ResourceLocation,
    tier: ITier,
) : MetaTileEntity(metaTileEntityId, tier, bufferValidInputModes, validOutputModesLists[1], "distributor") {

    private val groupX = when (tier.numeric) {
        7, 8, 9 -> tier.numeric - 5
        else -> 1
    }
    private val groupY = when (tier.numeric) {
        7, 8 -> 2
        9 -> 3
        else -> 1
    }

    private val groups = List(groupX * groupY) { ClayiumItemStackHandler(this, 4) }

    override val itemInventory = CombinedInvWrapper(*groups.toTypedArray())
    override val importItems = itemInventory
    override val exportItems = itemInventory

    private var groupIndex = 0

    @Suppress("unused")
    private val ioHandler = DistributorIoHandler()

    override fun buildUI(data: MetaTileEntityGuiData, syncManager: GuiSyncManager): ModularPanel {
        val height = GUI_DEFAULT_HEIGHT - 50 + (18*2 * groupY + 2 * (groupY - 1))
        return ModularPanel.defaultPanel(translationKey, GUI_DEFAULT_WIDTH, height)
            .columnWithPlayerInv {
                child(buildMainParentWidget(syncManager))
            }
    }

    override fun buildMainParentWidget(syncManager: GuiSyncManager): ParentWidget<*> {
        val groups = groups.mapIndexed { i, handler ->
            syncManager.registerSlotGroup("group$i", 2)
            val group = SlotGroupWidget.builder()
                .matrix("II", "II")
                .key('I') { j ->
                    ItemSlot().slot(SyncHandlers.itemSlot(handler, j).slotGroup("group$i"))
                }
                .build()
            group
        }
        val slotGroupRows = groups.windowed(this.groupX, this.groupX).map { slotGroupList ->
            val row = Row()
                .size((18 * 2) * this.groupX + 2 * (this.groupX - 1), 18 * 2)
            slotGroupList.forEachIndexed { i, g ->
                row.child(g.marginLeft(2 * min(i, 1)))
            }
            row
        }
        val column = Column()
            .width(18 * 2 * this.groupX + 2 * (this.groupX - 1))
            .height(18 * 2 * this.groupY + 2 * (this.groupY - 1))
        val w = super.buildMainParentWidget(syncManager)
        slotGroupRows.forEachIndexed { i, row ->
            column.child(row.marginTop(2 * min(i, 1)))
        }
        return w.child(column.align(Alignment.Center))
    }

    override fun <T> getCapability(capability: Capability<T>, facing: EnumFacing?): T? {
        if (capability === CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            // don't expose inventory
            return null
        }
        return super.getCapability(capability, facing)
    }

    override fun createMetaTileEntity(): MetaTileEntity {
        return DistributorMetaTileEntity(this.metaTileEntityId, this.tier)
    }

    /**
     * imported or exported -> pointer++
     * if the exportation is one lap behind, stop importing
     */
    private inner class DistributorIoHandler : AutoIoHandler.Combined(this@DistributorMetaTileEntity, isBuffer = true) {
        private var oneLapBehind = false
        private var importPtr = 0
            set(value) {
                field = value
                if (field >= groups.size) field = 0
                this.oneLapBehind = (field == exportPtr)
            }
        private var exportPtr = 0
            set(value) {
                field = value
                if (field >= groups.size) field = 0
                this.oneLapBehind = false
            }
        private var lastDirection = EnumFacing.DOWN

        override fun serializeNBT(): NBTTagCompound {
            return super.serializeNBT().apply {
                setInteger("importPtr", importPtr)
                setInteger("exportPtr", exportPtr)
                setBoolean("oneLapBehind", oneLapBehind)
                setInteger("lastDirection", lastDirection.index)
            }
        }

        override fun deserializeNBT(data: NBTTagCompound) {
            super.deserializeNBT(data)
            importPtr = data.getInteger("importPtr")
            exportPtr = data.getInteger("exportPtr")
            oneLapBehind = data.getBoolean("oneLapBehind")
            lastDirection = EnumFacing.byIndex(data.getInteger("lastDirection"))
        }

        override fun importFromNeighbors() {
            if (oneLapBehind) return
            var remainingImport = amountPerAction
            val importItems = groups[importPtr]
            for (side in EnumFacing.entries) {
                if (!(remainingImport > 0 && isImporting(side))) continue
                remainingImport = transferItemStack(
                    from = metaTileEntity.getNeighbor(side)?.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, side.opposite) ?: continue,
                    to = importItems,
                    amount = remainingImport,
                )
            }
            if (remainingImport != amountPerAction) importPtr++
        }

        override fun exportToNeighbors() {
            var remainingExport = amountPerAction
            val exportItems = groups[exportPtr]
            val neighbors = EnumFacing.entries.enumMapNotNull { side ->
                if (!isExporting(side)) return@enumMapNotNull null
                getNeighbor(side)?.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, side.opposite)
            }
            @Suppress("UsePropertyAccessSyntax") //synthetic properties
            if (neighbors.isEmpty()) return
            for (i in 0..<exportItems.slots) {
                val exported = exportItems.extractItem(i, remainingExport, true)
                if (exported.isEmpty) continue

                val countPerNeighbor = exported.count / neighbors.size
                val toInsert = exported.copyWithSize(countPerNeighbor)
                var notInserted = 0
                if (countPerNeighbor != 0) {
                    for ((side, neighbor) in neighbors) {
                        val remain = ItemHandlerHelper.insertItem(neighbor, toInsert, false)
                        val inserted = toInsert.count - remain.count
                        exportItems.extractItem(i, inserted, false)
                        remainingExport -= inserted
                        notInserted += remain.count
                        if (!remain.isEmpty) {
                            neighbors.remove(side)
                        }
                    }
                }

                @Suppress("UsePropertyAccessSyntax") //synthetic properties
                if (neighbors.isEmpty()) continue

                val iter = generateSequence(lastDirection.next()) { current ->
                    @Suppress("UsePropertyAccessSyntax") //synthetic properties
                    if (neighbors.isEmpty()) {
                        return@generateSequence null
                    }
                    else {
                        for (i in 0..<6) {
                            val next = current.next()
                            if (neighbors.containsKey(next)) {
                                return@generateSequence next
                            }
                        }
                        return@generateSequence null
                    }
                }.iterator()

                notInserted += exported.count % neighbors.size
                val toInsertCount1 = exported.copyWithSize(1)
                while (remainingExport > 0 && notInserted > 0 && iter.hasNext()) {
                    val side = iter.next()
                    lastDirection = side
                    val handler = neighbors[side] ?: continue
                    val remain = ItemHandlerHelper.insertItem(handler, toInsertCount1, false)
                    if (!remain.isEmpty) {
                        neighbors.remove(side)
                        continue
                    }
                    exportItems.extractItem(i, 1, false)
                    notInserted--
                    remainingExport--
                }
            }
            if (remainingExport != amountPerAction) exportPtr++
        }
    }
}