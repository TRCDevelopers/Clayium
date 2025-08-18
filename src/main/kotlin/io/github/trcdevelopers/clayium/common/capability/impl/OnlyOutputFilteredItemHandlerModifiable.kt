package io.github.trcdevelopers.clayium.common.capability.impl

import io.github.trcdevelopers.clayium.api.capability.IItemFilter
import net.minecraft.item.ItemStack
import net.minecraftforge.items.IItemHandlerModifiable

class OnlyOutputFilteredItemHandlerModifiable(
    private val delegate: IItemHandlerModifiable,
    private val filter: (Int, ItemStack) -> Boolean,
) : IItemHandlerModifiable by delegate {

    constructor(delegate: IItemHandlerModifiable, filter: (ItemStack) -> Boolean) : this(delegate, { _, stack -> filter(stack) })
    constructor(delegate: IItemHandlerModifiable, filter: IItemFilter) : this(delegate, filter::test)

    override fun insertItem(slot: Int, stack: ItemStack, simulate: Boolean): ItemStack {
        if (filter(slot, stack)) {
            return delegate.insertItem(slot, stack, simulate)
        }
        return stack
    }

    override fun isItemValid(slot: Int, stack: ItemStack): Boolean {
        return filter(slot, stack) && delegate.isItemValid(slot, stack)
    }
}