package com.github.trcdevelopers.clayium.common.util

import net.minecraft.item.ItemStack
import net.minecraftforge.items.IItemHandlerModifiable

object TransferUtils {
    fun insertToHandler(handler: IItemHandlerModifiable, stacks: List<ItemStack>) {
        for (stack in stacks) {
            var remain: ItemStack = stack
            for (i in 0..<handler.slots) {
                remain = handler.insertItem(i, remain, false)
                if (remain.isEmpty) break
            }
        }
    }
}