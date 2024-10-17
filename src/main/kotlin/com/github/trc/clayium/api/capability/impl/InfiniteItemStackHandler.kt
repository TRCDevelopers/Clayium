package com.github.trc.clayium.api.capability.impl

import net.minecraft.item.ItemStack
import net.minecraftforge.items.IItemHandlerModifiable

class InfiniteItemStackHandler(
    private val stack: ItemStack,
) : IItemHandlerModifiable {
    override fun getSlots() = 1

    override fun getStackInSlot(slot: Int): ItemStack {
        return stack.copy()
    }

    override fun insertItem(slot: Int, stack: ItemStack, simulate: Boolean): ItemStack {
        return stack
    }

    override fun extractItem(slot: Int, amount: Int, simulate: Boolean): ItemStack {
        return stack.copy().apply { count = amount.coerceAtMost(getSlotLimit(0)) }
    }

    override fun getSlotLimit(slot: Int) = stack.maxStackSize

    // for gui
    override fun setStackInSlot(slot: Int, stack: ItemStack) {}
}
