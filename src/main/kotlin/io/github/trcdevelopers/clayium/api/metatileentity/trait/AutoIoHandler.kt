package io.github.trcdevelopers.clayium.api.metatileentity.trait

import io.github.trcdevelopers.clayium.api.capability.ClayiumDataCodecs
import io.github.trcdevelopers.clayium.api.capability.ClayiumTileCapabilities
import io.github.trcdevelopers.clayium.api.metatileentity.MTETrait
import io.github.trcdevelopers.clayium.api.metatileentity.MetaTileEntity
import io.github.trcdevelopers.clayium.api.util.MachineIoMode
import io.github.trcdevelopers.clayium.common.config.ConfigTierBalance
import net.minecraft.util.EnumFacing
import net.minecraftforge.common.capabilities.Capability
import net.minecraftforge.items.CapabilityItemHandler
import net.minecraftforge.items.IItemHandler
import net.minecraftforge.items.ItemHandlerHelper

abstract class AutoIoHandler(
    metaTileEntity: MetaTileEntity,
    val isBuffer: Boolean = false,
    traitName: String = ClayiumDataCodecs.AUTO_IO_HANDLER,
    tier: Int = metaTileEntity.tier.numeric,
) : MTETrait(metaTileEntity, traitName) {

    val coolTime = if (isBuffer) ConfigTierBalance.bufferInterval[tier] else ConfigTierBalance.machineInterval[tier]
    val amountPerAction = if (isBuffer) ConfigTierBalance.bufferAmount[tier] else ConfigTierBalance.machineAmount[tier]

    private var remainTransferImport = 0
    private var remainTransferExport = 0

    protected var ticked = 0

    /**
     * Indicates whether the handler is currently transferring items.
     * It will be true for a tick when the interval is reached.
     */
    var transferring = false
        private set

    protected abstract fun transferItems(amount: Int)

    protected fun imported(amount: Int) {
        this.remainTransferImport -= amount
    }

    protected fun exported(amount: Int) {
        this.remainTransferExport -= amount
    }

    override fun update() {
        super.update()
        if (metaTileEntity.isRemote) return
        if (this.transferring) {
            this.transferring = false
        }

        if (++ticked >= coolTime) {
            this.transferring = true
            this.remainTransferImport = this.amountPerAction
            this.remainTransferExport = this.amountPerAction
            transferItems(this.amountPerAction)
            ticked = 0
        }
    }

    /**
     * Re-attempts to transfer items within the current tick.
     * @return true if more items can be transferred, false otherwise.
     */
    fun reTransferWithinTick(): Boolean {
        if (!this.transferring) return false
        if (this.remainTransferImport > 0) {
            this.importFromNeighbors(this.remainTransferImport)
        }
        if (this.remainTransferExport > 0) {
            this.exportToNeighbors(this.remainTransferExport)
        }
        // remainTransfer is mutated in import/export methods
        val canImportMore = this.remainTransferImport > 0
        val canExportMore = this.remainTransferExport > 0
        return canImportMore || canExportMore
    }

    protected open fun isImporting(side: EnumFacing): Boolean = metaTileEntity.getInput(side) != MachineIoMode.NONE
    protected open fun isExporting(side: EnumFacing): Boolean = metaTileEntity.getOutput(side) != MachineIoMode.NONE

    protected open fun getImportItems(side: EnumFacing): IItemHandler? = metaTileEntity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, side)
    protected open fun getExportItems(side: EnumFacing): IItemHandler? = metaTileEntity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, side)

    protected open fun importFromNeighbors(amount: Int) {
        var remainingImport = amount
        for (side in EnumFacing.entries) {
            if (remainingImport > 0 && isImporting(side)) {
                remainingImport = transferItemStack(
                    from = metaTileEntity.getNeighborTileEntity(side)?.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, side.opposite) ?: continue,
                    to = getImportItems(side) ?: continue,
                    amount = remainingImport,
                )
            }
        }
        if (remainingImport < amount) {
            this.imported(amount - remainingImport)
        }
    }

    protected open fun exportToNeighbors(amount: Int) {
        var remainingExport = amount
        for (side in EnumFacing.entries) {
            if (remainingExport > 0 && isExporting(side)) {
                remainingExport = transferItemStack(
                    from = getExportItems(side) ?: continue,
                    to = metaTileEntity.getNeighborTileEntity(side)?.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, side.opposite) ?: continue,
                    amount = remainingExport,
                )
            }
        }
        if (remainingExport < amount) {
            this.exported(amount - remainingExport)
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

    override fun <T> getCapability(capability: Capability<T>, facing: EnumFacing?): T? {
        if (capability === ClayiumTileCapabilities.AUTO_IO_HANDLER) {
            return capability.cast(this)
        }
        return super.getCapability(capability, facing)
    }

    open class Importer(
        metaTileEntity: MetaTileEntity,
        isBuffer: Boolean = false,
        traitName : String = ClayiumDataCodecs.AUTO_IO_HANDLER,
        tier: Int = metaTileEntity.tier.numeric,
    ) : AutoIoHandler(metaTileEntity, isBuffer, traitName, tier) {
        override fun transferItems(amount: Int) {
            importFromNeighbors(amount)
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
        override fun transferItems(amount: Int) {
            exportToNeighbors(amount)
        }
    }

    open class Combined(
        metaTileEntity: MetaTileEntity,
        isBuffer: Boolean = false,
        tier: Int = metaTileEntity.tier.numeric,
    ) : AutoIoHandler(metaTileEntity, isBuffer, ClayiumDataCodecs.AUTO_IO_HANDLER, tier) {
        override fun transferItems(amount: Int) {
            importFromNeighbors(amount)
            exportToNeighbors(amount)
        }
    }
}