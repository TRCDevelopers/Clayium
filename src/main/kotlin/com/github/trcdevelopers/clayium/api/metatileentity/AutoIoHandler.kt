package com.github.trcdevelopers.clayium.api.metatileentity

import com.github.trcdevelopers.clayium.api.capability.ClayiumDataCodecs
import com.github.trcdevelopers.clayium.common.blocks.machine.MachineIoMode
import com.github.trcdevelopers.clayium.common.config.ConfigTierBalance
import net.minecraft.item.ItemStack
import net.minecraft.util.EnumFacing
import net.minecraftforge.items.CapabilityItemHandler
import net.minecraftforge.items.IItemHandler

abstract class AutoIoHandler(
    metaTileEntity: MetaTileEntity,
    isBuffer: Boolean = false,
    traitName: String = ClayiumDataCodecs.AUTO_IO_HANDLER,
) : MTETrait(metaTileEntity, traitName) {

    protected val intervalTick = if (isBuffer) ConfigTierBalance.bufferInterval[metaTileEntity.tier.numeric] else ConfigTierBalance.machineInterval[metaTileEntity.tier.numeric]
    protected val amountPerAction = if (isBuffer) ConfigTierBalance.bufferAmount[metaTileEntity.tier.numeric] else ConfigTierBalance.machineAmount[metaTileEntity.tier.numeric]

    protected var ticked = 0

    protected open fun isImporting(side: EnumFacing): Boolean = metaTileEntity.getInput(side).allowAutoIo
    protected open fun isExporting(side: EnumFacing): Boolean = metaTileEntity.getOutput(side).allowAutoIo

    protected fun importFromNeighbors(importItems: IItemHandler) {
        var remainingImport = amountPerAction
        for (side in EnumFacing.entries) {
            if (remainingImport > 0 && isImporting(side)) {
                remainingImport -= transferItemStack(
                    from = metaTileEntity.getNeighbor(side)?.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, side.opposite) ?: continue,
                    to = importItems,
                    amount = remainingImport,
                )
            }
        }
    }

    protected fun exportToNeighbors(exportItems: IItemHandler) {
        var remainingExport = amountPerAction
        for (side in EnumFacing.entries) {
            if (remainingExport > 0 && isExporting(side)) {
                remainingExport -= transferItemStack(
                    from = exportItems,
                    to = metaTileEntity.getNeighbor(side)?.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, side.opposite) ?: continue,
                    amount = remainingExport,
                )
            }
        }
    }

    protected fun transferItemStack(
        from: IItemHandler,
        to: IItemHandler,
        amount: Int,
    ) : Int {
        var remainingWork = amount

        for (i in 0..<from.slots) {
            val extracted = from.extractItem(i, remainingWork, true)
                .takeUnless { it.isEmpty } ?: continue
            val remain = insertToInventory(to, extracted, true)

            val stackToInsert = from.extractItem(i, extracted.count - remain.count, false)
            insertToInventory(to, stackToInsert, false)
            remainingWork -= extracted.count - remain.count
            if (remainingWork <= 0) break
        }
        return remainingWork
    }

    /**
     * @param handler The target inventory of the insertion
     * @param stack The stack to insert. This stack will not be modified.
     * @return The remaining ItemStack that was not inserted (if the entire stack is accepted, then return an empty ItemStack)
     */
    protected fun insertToInventory(handler: IItemHandler, stack: ItemStack, simulate: Boolean): ItemStack {
        var remaining = stack.copy()
        for (i in 0..<handler.slots) {
            remaining = handler.insertItem(i, remaining, simulate)
            if (remaining.isEmpty) break
        }
        return remaining
    }

    open class Importer(
        metaTileEntity: MetaTileEntity,
        private val target: IItemHandler = metaTileEntity.importItems,
        isBuffer: Boolean = false,
        traitName : String = ClayiumDataCodecs.AUTO_IO_HANDLER,
    ) : AutoIoHandler(metaTileEntity, isBuffer, traitName) {
        override fun update() {
            if (metaTileEntity.world?.isRemote == true) return
            if (ticked++ < intervalTick) return
            importFromNeighbors(target)
            ticked = 0
        }
    }

    class EcImporter(
        metaTileEntity: MetaTileEntity,
        private val energizedClayItemHandler: IItemHandler = metaTileEntity.importItems,
    ) : Importer(metaTileEntity, energizedClayItemHandler, false, traitName = "${ClayiumDataCodecs.AUTO_IO_HANDLER}.${ClayiumDataCodecs.ENERGY_HOLDER}") {
        override fun isImporting(side: EnumFacing): Boolean {
            return metaTileEntity.getInput(side) == MachineIoMode.CE
        }
    }

    open class Exporter(
        metaTileEntity: MetaTileEntity,
        private val target: IItemHandler = metaTileEntity.exportItems,
        isBuffer: Boolean = false,
        traitName: String = ClayiumDataCodecs.AUTO_IO_HANDLER,
    ) : AutoIoHandler(metaTileEntity, isBuffer, traitName) {
        override fun update() {
            if (metaTileEntity.world?.isRemote == true) return
            if (ticked++ < intervalTick) return
            exportToNeighbors(target)
            ticked = 0
        }
    }

    class Combined(metaTileEntity: MetaTileEntity, isBuffer: Boolean = false) : AutoIoHandler(metaTileEntity, isBuffer) {
        private val importItems = metaTileEntity.importItems
        private val exportItems = metaTileEntity.exportItems

        override fun update() {
            if (metaTileEntity.world?.isRemote == true) return
            if (ticked++ < intervalTick) return
            importFromNeighbors(importItems)
            exportToNeighbors(exportItems)
            ticked = 0
        }
    }
}