package com.github.trc.clayium.api.metatileentity.trait

import com.github.trc.clayium.api.capability.ClayiumDataCodecs
import com.github.trc.clayium.api.metatileentity.MTETrait
import com.github.trc.clayium.api.metatileentity.MetaTileEntity
import com.github.trc.clayium.api.util.MachineIoMode
import com.github.trc.clayium.common.config.ConfigTierBalance
import net.minecraft.util.EnumFacing
import net.minecraftforge.items.CapabilityItemHandler
import net.minecraftforge.items.IItemHandler
import net.minecraftforge.items.ItemHandlerHelper
import kotlin.Int

abstract class AutoIoHandler(
    metaTileEntity: MetaTileEntity,
    isBuffer: Boolean = false,
    traitName: String = ClayiumDataCodecs.AUTO_IO_HANDLER,
    tier: Int = metaTileEntity.tier.numeric,
) : MTETrait(metaTileEntity, traitName) {

    protected val coolTime = if (isBuffer) ConfigTierBalance.bufferInterval[tier] else ConfigTierBalance.machineInterval[tier]
    protected val amountPerAction = if (isBuffer) ConfigTierBalance.bufferAmount[tier] else ConfigTierBalance.machineAmount[tier]

    protected var ticked = 0

    protected abstract fun transferItems()

    override fun update() {
        super.update()
        if (metaTileEntity.isRemote) return

        if (++ticked >= coolTime) {
            transferItems()
            ticked = 0
        }
    }

    protected open fun isImporting(side: EnumFacing): Boolean = metaTileEntity.getInput(side) != MachineIoMode.NONE
    protected open fun isExporting(side: EnumFacing): Boolean = metaTileEntity.getOutput(side) != MachineIoMode.NONE

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
        tier: Int = metaTileEntity.tier.numeric,
    ) : AutoIoHandler(metaTileEntity, isBuffer, traitName, tier) {
        override fun transferItems() {
            importFromNeighbors()
        }
    }

    /**
     * this exists to separate limits (coolTime and amountPerAction) from the normal importer.
     * even if the normal importer is working at full speed (and cannot import energized clay because of its limit),
     * Energized Clay should be imported.
     */
    class EcImporter(
        metaTileEntity: MetaTileEntity,
        private val energizedClayItemHandler: IItemHandler = metaTileEntity.importItems,
    ) : Importer(metaTileEntity, false, traitName = "${ClayiumDataCodecs.AUTO_IO_HANDLER}.${ClayiumDataCodecs.CLAY_ENERGY_HOLDER}") {
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
        tier: Int = metaTileEntity.tier.numeric,
    ) : AutoIoHandler(metaTileEntity, isBuffer, ClayiumDataCodecs.AUTO_IO_HANDLER, tier) {
        override fun transferItems() {
            exportToNeighbors()
        }
    }

    open class Combined(
        metaTileEntity: MetaTileEntity,
        isBuffer: Boolean = false,
        tier: Int = metaTileEntity.tier.numeric,
    ) : AutoIoHandler(metaTileEntity, isBuffer, ClayiumDataCodecs.AUTO_IO_HANDLER, tier) {
        override fun transferItems() {
            importFromNeighbors()
            exportToNeighbors()
        }
    }
}