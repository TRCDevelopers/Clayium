package io.github.trcdevelopers.clayium.common.util

import net.minecraft.item.ItemStack

fun ItemStack.isIdenticalTo(other: ItemStack): Boolean {
    return this.isItemEqual(other) && ItemStack.areItemStackTagsEqual(this, other)
}