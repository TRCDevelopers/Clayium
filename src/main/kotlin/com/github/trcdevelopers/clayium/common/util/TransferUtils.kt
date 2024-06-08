package com.github.trcdevelopers.clayium.common.util

import net.minecraft.item.ItemStack
import net.minecraftforge.items.IItemHandlerModifiable

object TransferUtils {
    /**
     * Insert a list of ItemStacks to an IItemHandlerModifiable
     * @param simulate if true, the operation will be simulated. default = false
     * @return true if all stacks are inserted successfully
     */
    fun insertToHandler(handler: IItemHandlerModifiable, stacks: List<ItemStack>, simulate: Boolean = false): Boolean {
        if (simulate) {
            for (stack in stacks) {
                var remain: ItemStack = stack
                for (i in 0..<handler.slots) {
                    remain = handler.insertItem(i, remain, true)
                    if (remain.isEmpty) break
                }
                if (!remain.isEmpty) return false
            }
            return true
        } else {
            var allStackInserted = true
            for (stack in stacks) {
                var remain: ItemStack = stack
                for (i in 0..<handler.slots) {
                    remain = handler.insertItem(i, remain, false)
                    if (remain.isEmpty) break
                }
                allStackInserted = allStackInserted && remain.isEmpty
            }
            return allStackInserted
        }
    }
}