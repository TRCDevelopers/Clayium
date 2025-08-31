package io.github.trcdevelopers.clayium.api.capability.impl

import io.github.trcdevelopers.clayium.api.capability.IItemFilter
import net.minecraft.item.ItemStack
import net.minecraftforge.items.IItemHandlerModifiable

/**
 * [IItemHandlerModifiable] version of [FilteredItemHandler].
 * Note: [setStackInSlot] will not be filtered.
 */
class FilteredItemHandlerModifiable(
    private val modifiable: IItemHandlerModifiable,
    filter: (Int, ItemStack) -> Boolean
) : FilteredItemHandler(modifiable, filter), IItemHandlerModifiable {

    constructor(delegate: IItemHandlerModifiable, filter: (ItemStack) -> Boolean) : this(delegate, { _, stack -> filter(stack) })
    constructor(delegate: IItemHandlerModifiable, filter: IItemFilter) : this(delegate, filter::test)

    override fun setStackInSlot(slot: Int, stack: ItemStack) {
        modifiable.setStackInSlot(slot, stack)
    }
}