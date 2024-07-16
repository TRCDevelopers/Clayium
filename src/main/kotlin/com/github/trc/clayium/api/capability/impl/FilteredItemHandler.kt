package com.github.trc.clayium.api.capability.impl

import com.github.trc.clayium.api.capability.IItemFilter
import net.minecraft.item.ItemStack
import net.minecraftforge.items.IItemHandler

class FilteredItemHandler(
    private val delegate: IItemHandler,
    private val filter: (ItemStack) -> Boolean,
) : IItemHandler by delegate {

    constructor(delegate: IItemHandler, filter: IItemFilter) : this(delegate, filter::test)

    override fun insertItem(slot: Int, stack: ItemStack, simulate: Boolean): ItemStack {
        if (filter(stack)) {
            return delegate.insertItem(slot, stack, simulate)
        }
        return stack
    }

    override fun extractItem(slot: Int, amount: Int, simulate: Boolean): ItemStack {
        if (filter(delegate.extractItem(slot, amount, true))) {
            return delegate.extractItem(slot, amount, simulate)
        }
        return ItemStack.EMPTY
    }
}