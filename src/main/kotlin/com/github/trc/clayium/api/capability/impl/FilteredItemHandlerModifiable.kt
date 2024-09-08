package com.github.trc.clayium.api.capability.impl

import com.github.trc.clayium.api.capability.IItemFilter
import net.minecraft.item.ItemStack
import net.minecraftforge.items.IItemHandlerModifiable

/**
 * [IItemHandlerModifiable] version of [FilteredItemHandler].
 * Note: [setStackInSlot] will not be filtered.
 */
class FilteredItemHandlerModifiable(
    private val modifiable: IItemHandlerModifiable,
    filter: (ItemStack) -> Boolean
) : FilteredItemHandler(modifiable, filter), IItemHandlerModifiable {
    constructor(delegate: IItemHandlerModifiable, filter: IItemFilter) : this(delegate, filter::test)

    override fun setStackInSlot(slot: Int, stack: ItemStack) {
        modifiable.setStackInSlot(slot, stack)
    }
}