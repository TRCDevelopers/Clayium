package com.github.trc.clayium.api.capability.impl

import com.github.trc.clayium.api.capability.IItemFilter
import net.minecraft.item.ItemStack
import net.minecraftforge.items.IItemHandler

class FilteredItemHandler(
    private val delegate: IItemHandler,
    private val filter: IItemFilter,
) : IItemHandler by delegate {
    override fun insertItem(slot: Int, stack: ItemStack, simulate: Boolean): ItemStack {
        if (filter.test(stack)) {
            return delegate.insertItem(slot, stack, simulate)
        }
        return stack
    }

    override fun extractItem(slot: Int, amount: Int, simulate: Boolean): ItemStack {
        if (filter.test(delegate.extractItem(slot, amount, true))) {
            return delegate.extractItem(slot, amount, simulate)
        }
        return ItemStack.EMPTY
    }
}