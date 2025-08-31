package io.github.trcdevelopers.clayium.api.capability.impl

import io.github.trcdevelopers.clayium.api.capability.IItemFilter
import net.minecraft.item.ItemStack
import net.minecraftforge.items.IItemHandler

open class FilteredItemHandler(
    private val delegate: IItemHandler,
    private val filter: (Int, ItemStack) -> Boolean,
) : IItemHandler by delegate {

    constructor(delegate: IItemHandler, filter: (ItemStack) -> Boolean) : this(delegate, { _, stack -> filter(stack) })
    constructor(delegate: IItemHandler, filter: IItemFilter) : this(delegate, filter::test)

    override fun insertItem(slot: Int, stack: ItemStack, simulate: Boolean): ItemStack {
        if (filter(slot, stack)) {
            return delegate.insertItem(slot, stack, simulate)
        }
        return stack
    }

    override fun extractItem(slot: Int, amount: Int, simulate: Boolean): ItemStack {
        if (filter(slot, delegate.extractItem(slot, amount, true))) {
            return delegate.extractItem(slot, amount, simulate)
        }
        return ItemStack.EMPTY
    }

    override fun isItemValid(slot: Int, stack: ItemStack): Boolean {
        return filter(slot, stack) && delegate.isItemValid(slot, stack)
    }
}