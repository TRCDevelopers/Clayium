package com.github.trc.clayium.api.metatileentity

import com.github.trc.clayium.api.capability.ClayiumDataCodecs
import com.github.trc.clayium.api.util.MachineIoMode
import com.github.trc.clayium.common.config.ConfigTierBalance
import net.minecraft.util.EnumFacing
import net.minecraftforge.items.CapabilityItemHandler
import net.minecraftforge.items.IItemHandler
import net.minecraftforge.items.ItemHandlerHelper

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

    protected open fun getImportItems(side: EnumFacing): IItemHandler? = metaTileEntity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, side)
    protected open fun getExportItems(side: EnumFacing): IItemHandler? = metaTileEntity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, side)

    protected open fun importFromNeighbors() {
        var remainingImport = amountPerAction
        for (side in EnumFacing.entries) {
            if (remainingImport > 0 && isImporting(side)) {
                remainingImport = transferItemStack(
                    from = metaTileEntity.getNeighbor(side)?.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, side.opposite) ?: continue,
                    to = getImportItems(side) ?: continue,
                    amount = remainingImport,
                )
            }
        }
    }

    protected open fun exportToNeighbors() {
        var remainingExport = amountPerAction
        for (side in EnumFacing.entries) {
            if (remainingExport > 0 && isExporting(side)) {
                remainingExport = transferItemStack(
                    from = getExportItems(side) ?: continue,
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
            val remain = ItemHandlerHelper.insertItem(to, extracted, true)

            val stackToInsert = from.extractItem(i, extracted.count - remain.count, false)
            ItemHandlerHelper.insertItem(to, stackToInsert, false)
            remainingWork -= extracted.count - remain.count
            if (remainingWork <= 0) break
        }
        return remainingWork
    }

    open class Importer(
        metaTileEntity: MetaTileEntity,
        isBuffer: Boolean = false,
        traitName : String = ClayiumDataCodecs.AUTO_IO_HANDLER,
    ) : AutoIoHandler(metaTileEntity, isBuffer, traitName) {
        override fun update() {
            if (metaTileEntity.isRemote) return
            if (ticked++ < intervalTick) return
            importFromNeighbors()
            ticked = 0
        }
    }

    class EcImporter(
        metaTileEntity: MetaTileEntity,
        private val energizedClayItemHandler: IItemHandler = metaTileEntity.importItems,
    ) : Importer(metaTileEntity, false, traitName = "${ClayiumDataCodecs.AUTO_IO_HANDLER}.${ClayiumDataCodecs.ENERGY_HOLDER}") {
        override fun isImporting(side: EnumFacing): Boolean {
            return metaTileEntity.getInput(side) == MachineIoMode.CE
        }

        override fun getImportItems(side: EnumFacing): IItemHandler? {
            return energizedClayItemHandler
        }
    }

    open class Exporter(
        metaTileEntity: MetaTileEntity,
        isBuffer: Boolean = false,
        traitName: String = ClayiumDataCodecs.AUTO_IO_HANDLER,
    ) : AutoIoHandler(metaTileEntity, isBuffer, traitName) {
        override fun update() {
            if (metaTileEntity.world?.isRemote == true) return
            if (ticked++ < intervalTick) return
            exportToNeighbors()
            ticked = 0
        }
    }

    open class Combined(metaTileEntity: MetaTileEntity, isBuffer: Boolean = false) : AutoIoHandler(metaTileEntity, isBuffer) {
        override fun update() {
            if (metaTileEntity.world?.isRemote == true) return
            if (ticked++ < intervalTick) return
            importFromNeighbors()
            exportToNeighbors()
            ticked = 0
        }
    }
}