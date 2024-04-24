package com.github.trcdevelopers.clayium.common.tileentity

import com.github.trcdevelopers.clayium.common.Clayium
import com.github.trcdevelopers.clayium.common.config.ConfigTierBalance
import com.github.trcdevelopers.clayium.common.tileentity.trait.TETrait
import net.minecraft.item.ItemStack
import net.minecraft.util.EnumFacing
import net.minecraftforge.items.CapabilityItemHandler
import net.minecraftforge.items.IItemHandler

abstract class AutoIoHandler(
    protected val tile: TileEntityMachine,
    isBuffer: Boolean = false,
) : TETrait(tile, tile.tier) {

    protected val intervalTick = if (isBuffer) ConfigTierBalance.bufferInterval[tile.tier] else ConfigTierBalance.machineInterval[tile.tier]
    protected val amountPerAction = if (isBuffer) ConfigTierBalance.bufferAmount[tile.tier] else ConfigTierBalance.machineAmount[tile.tier]

    protected var ticked = 0

    protected open fun isImporting(side: EnumFacing): Boolean = tile.getInput(side).allowAutoIo
    protected open fun isExporting(side: EnumFacing): Boolean = tile.getOutput(side).allowAutoIo

    abstract override fun update()

    protected fun transferItemStack(
        from: IItemHandler,
        to: IItemHandler,
        amount: Int,
    ) : Int {
        var remainingWork = amount

        for (i in 0..<from.slots) {
            val extracted = from.extractItem(i, remainingWork, false)
                .takeUnless { it.isEmpty } ?: continue
            val remain = insertToInventory(to, extracted, false)
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

    open class Importer(tile: TileEntityMachine, private val target: IItemHandler = tile.inputInventory, isBuffer: Boolean = false) : AutoIoHandler(tile, isBuffer) {
        override fun update() {
            if (tile.world?.isRemote == true) return
            ticked++
            if (ticked < intervalTick) return
            var remainingImport = amountPerAction
            for (side in EnumFacing.entries) {
                if (remainingImport > 0 && isImporting(side)) {
                    remainingImport -= transferItemStack(
                        from = tile.getNeighbor(side)?.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, side.opposite) ?: continue,
                        to = target,
                        amount = remainingImport,
                    )
                }
            }
            ticked = 0
        }
    }
    open class Exporter(tile: TileEntityMachine, private val target: IItemHandler = tile.outputInventory, isBuffer: Boolean = false) : AutoIoHandler(tile, isBuffer) {
        override fun update() {
            if (tile.world?.isRemote == true) return
            ticked++
            if (ticked < intervalTick) return
            var remainingExport = amountPerAction
            for (side in EnumFacing.entries) {
                if (remainingExport > 0 && isExporting(side)) {
                    remainingExport -= transferItemStack(
                        from = target,
                        to = tile.getNeighbor(side)?.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, side.opposite) ?: continue,
                        amount = remainingExport,
                    )
                }
            }
            ticked = 0
        }
    }
    class Combined(tile: TileEntityMachine, isBuffer: Boolean = false) : AutoIoHandler(tile, isBuffer) {
        private val importer = Importer(tile)
        private val exporter = Exporter(tile)

        override fun update() {
            importer.update()
            exporter.update()
        }
    }
}