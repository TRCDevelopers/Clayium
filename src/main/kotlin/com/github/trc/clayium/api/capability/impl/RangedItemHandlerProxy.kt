package com.github.trc.clayium.api.capability.impl

import net.minecraftforge.items.IItemHandlerModifiable
import net.minecraftforge.items.wrapper.RangedWrapper

class RangedItemHandlerProxy(
    private val itemHandler: IItemHandlerModifiable,
    private val range: IntRange,
) : IItemHandlerModifiable by RangedWrapper(itemHandler, range.first, range.last + 1) {
    constructor(
        itemHandler: IItemHandlerModifiable,
        availableSlot: Int
    ) : this(itemHandler, availableSlot..availableSlot)
}
