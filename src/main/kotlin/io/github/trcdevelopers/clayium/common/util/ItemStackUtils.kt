package io.github.trcdevelopers.clayium.common.util

import io.github.trcdevelopers.clayium.api.unification.stack.ItemAndMeta
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound

fun ItemStack.isIdenticalTo(other: ItemStack): Boolean {
    return this.isItemEqual(other) && ItemStack.areItemStackTagsEqual(this, other)
}

/**
 * Merges a list of ItemStacks by combining identical stacks.
 * **ItemStacks in the receiver list will not be modified.**
 *
 * Order is not guaranteed. While the current implementation preserves order, this behavior may change in the future.
 *
 * @return A new list of ItemStacks with merged identical stacks.
 */
fun List<ItemStack>.merge(): MutableList<ItemStack> {
    val map = Object2IntOpenHashMap<Pair<ItemAndMeta, NBTTagCompound?>>()
    for (stack in this) {
        if (stack.isEmpty) continue
        val itemAndMeta = ItemAndMeta(stack)
        val key = Pair(itemAndMeta, stack.tagCompound)
        map.addTo(key, stack.count)
    }
    val result = mutableListOf<ItemStack>()

    for ((key, count) in map.object2IntEntrySet()) {
        val (itemAndMeta, tag) = key
        val stack = ItemStack(itemAndMeta.item, 1, itemAndMeta.meta)
        if (stack.maxStackSize >= count) {
            stack.count = count
        } else {
            stack.count = count % stack.maxStackSize
            val fullStackItemCount = count / stack.maxStackSize
            for (i in 0..<fullStackItemCount) {
                result.add(ItemStack(itemAndMeta.item, stack.maxStackSize, itemAndMeta.meta).apply {
                    tagCompound = tag
                })
            }
        }
        if (!stack.isEmpty) {
            stack.tagCompound = tag
            result.add(stack)
        }
    }
    return result
}