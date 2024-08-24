package com.github.trc.clayium.common.util

import net.minecraft.item.ItemStack
import net.minecraftforge.items.IItemHandler
import net.minecraftforge.items.IItemHandlerModifiable
import net.minecraftforge.items.ItemHandlerHelper
import net.minecraftforge.items.ItemStackHandler

fun IItemHandler.transferTo(to: IItemHandler) {
    for (i in 0..<this.slots) {
        val sourceStack = this.extractItem(i, Int.MAX_VALUE, true)
        if (sourceStack.isEmpty) continue

        val remain = ItemHandlerHelper.insertItem(to, sourceStack, true)
        val amountToInsert = sourceStack.count - remain.count
        if (amountToInsert <= 0) continue

        val extracted = this.extractItem(i, amountToInsert, false)
        ItemHandlerHelper.insertItem(to, extracted, false)
    }
}

object TransferUtils {
    /**
     * Insert a list of ItemStacks to an IItemHandlerModifiable
     * @param simulate if true, the operation will be simulated. default = false
     * @return true if all stacks are inserted successfully
     */
    fun insertToHandler(handler: IItemHandlerModifiable, stacks: List<ItemStack>, simulate: Boolean = false): Boolean {
        if (simulate) {
            val copiedHandler = ItemStackHandler(handler.slots).apply {
                for (i in 0..<handler.slots) {
                    this.setStackInSlot(i, handler.getStackInSlot(i).copy())
                }
            }
            for (stack in stacks) {
                val remain = ItemHandlerHelper.insertItemStacked(copiedHandler, stack, false)
                if (!remain.isEmpty) return false
            }
            return true
        } else {
            var allStackInserted = true
            for (stack in stacks) {
                val remain = ItemHandlerHelper.insertItemStacked(handler, stack, false)
                if (!remain.isEmpty) return false
                allStackInserted = allStackInserted && remain.isEmpty
            }
            return allStackInserted
        }
    }
}