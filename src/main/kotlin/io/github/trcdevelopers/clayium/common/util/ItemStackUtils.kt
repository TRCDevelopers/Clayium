package io.github.trcdevelopers.clayium.common.util

import net.minecraft.item.ItemStack

fun ItemStack.isIdenticalTo(other: ItemStack): Boolean {
    return this.isItemEqual(other) && ItemStack.areItemStackTagsEqual(this, other)
}

object ItemStackUtils {
    fun isItemStackIdentical(stack1: ItemStack, stack2: ItemStack): Boolean {
        return stack1.isItemEqual(stack2) && ItemStack.areItemStackTagsEqual(stack1, stack2)
    }
}