package com.github.trcdeveloppers.clayium.common.blocks.machine

import net.minecraft.item.ItemStack
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.EnumFacing
import net.minecraftforge.common.capabilities.Capability
import net.minecraftforge.items.CapabilityItemHandler
import net.minecraftforge.items.IItemHandler

class ItemStackTransferHandler(
    private val transferInterval: Int,
    private val transferAmount: Int,
    private val inventory: IItemHandler,
    private val importingFaces: MutableSet<EnumFacing>,
    private val exportingFaces: MutableSet<EnumFacing>,
    private val tileEntity: TileEntity,
)  {
    private var ticked: Int = 0

    fun transfer() {
        if (tileEntity.isInvalid || tileEntity.world.isRemote) return
        if (ticked < transferInterval) {
            ticked++
            return
        }
        val world = tileEntity.world
        val pos = tileEntity.pos

        var remainingImportWork = this.transferAmount
        for (side in importingFaces) {
            // target -> inventory
            remainingImportWork -= this.transferItemStack(
                world.getTileEntity(pos.offset(side))?.getCapability(ITEM_HANDLER_CAPABILITY, side.opposite) ?: continue,
                this.inventory,
                this.transferAmount,
            )
            if (remainingImportWork <= 0) break
        }

        var remainingExportWork = this.transferAmount
        for (side in exportingFaces) {
            // inventory -> target
            remainingExportWork -= this.transferItemStack(
                this.inventory,
                world.getTileEntity(pos.offset(side))?.getCapability(ITEM_HANDLER_CAPABILITY, side.opposite) ?: continue,
                this.transferAmount,
            )
            if (remainingExportWork <= 0) break
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
            // get how many items can be inserted
            val extractedStack = from.extractItem(i, remainingWork, true)
            if (extractedStack.isEmpty) continue
            val insertedItemCount = extractedStack.count - insertToInventory(to, extractedStack, true).count
            if (insertedItemCount == 0) continue

            // actually insert the items
            insertToInventory(to, from.extractItem(i, insertedItemCount, false), false)
            remainingWork -= insertedItemCount
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

    companion object {
        private val ITEM_HANDLER_CAPABILITY: Capability<IItemHandler> = CapabilityItemHandler.ITEM_HANDLER_CAPABILITY
    }
}