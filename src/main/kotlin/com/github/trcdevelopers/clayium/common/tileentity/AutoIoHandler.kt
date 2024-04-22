package com.github.trcdevelopers.clayium.common.tileentity

import com.github.trcdevelopers.clayium.common.blocks.machine.MachineIoMode
import com.github.trcdevelopers.clayium.common.config.ConfigTierBalance
import net.minecraft.item.ItemStack
import net.minecraft.util.EnumFacing
import net.minecraftforge.items.CapabilityItemHandler
import net.minecraftforge.items.IItemHandler

class AutoIoHandler(
    private val tile: TileEntityMachine,
    isBuffer: Boolean = false,
) {

    private val inputInventory = tile.inputInventory
    private val outputInventory = tile.outputInventory

    private val intervalTick = if (isBuffer) ConfigTierBalance.bufferInterval[tile.tier] else ConfigTierBalance.machineInterval[tile.tier]
    private val amountPerAction = if (isBuffer) ConfigTierBalance.bufferAmount[tile.tier] else ConfigTierBalance.machineAmount[tile.tier]

    private var ticked = 0

    private fun isImporting(side: EnumFacing): Boolean = tile.getInput(side).allowAutoIo
    private fun isExporting(side: EnumFacing): Boolean = tile.getOutput(side).allowAutoIo

    fun tick() {
        if (tile.world.isRemote) return
        ticked++
        if (ticked < intervalTick) return
        var remainingImport = amountPerAction
        for (side in EnumFacing.entries) {
            if (remainingImport > 0 && isImporting(side)) {
                remainingImport -= transferItemStack(
                    tile.getNeighbor(side)?.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, side.opposite) ?: continue,
                    to = inputInventory,
                    amount = remainingImport,
                )
            }
        }
        var remainingExport = amountPerAction
        for (side in EnumFacing.entries) {
            if (remainingExport > 0 && isExporting(side)) {
                remainingExport -= transferItemStack(
                    outputInventory,
                    to = tile.getNeighbor(side)?.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, side.opposite) ?: continue,
                    amount = remainingExport,
                )
            }
        }
        ticked = 0
    }

    private fun transferItemStack(
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
        }
        return remainingWork
    }

    /**
     * @param handler The target inventory of the insertion
     * @param stack The stack to insert. This stack will not be modified.
     * @return The remaining ItemStack that was not inserted (if the entire stack is accepted, then return an empty ItemStack)
     */
    private fun insertToInventory(handler: IItemHandler, stack: ItemStack, simulate: Boolean): ItemStack {
        var remaining = stack.copy()
        for (i in 0..<handler.slots) {
            remaining = handler.insertItem(i, remaining, simulate)
            if (remaining.isEmpty) break
        }
        return remaining
    }
}