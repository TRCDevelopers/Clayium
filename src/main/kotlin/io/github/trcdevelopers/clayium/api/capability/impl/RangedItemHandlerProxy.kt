package io.github.trcdevelopers.clayium.api.capability.impl

import net.minecraft.item.ItemStack
import net.minecraftforge.items.IItemHandlerModifiable
import net.minecraftforge.items.wrapper.RangedWrapper

class RangedItemHandlerProxy(itemHandler: IItemHandlerModifiable, range: IntRange) : IItemHandlerModifiable {
    constructor(itemHandler: IItemHandlerModifiable, availableSlot: Int) : this(itemHandler, availableSlot..availableSlot)
    val delegate = RangedWrapper(itemHandler, range.first, range.last + 1)

    override fun getSlots() = delegate.slots
    override fun getStackInSlot(slot: Int) = delegate.getStackInSlot(slot)
    override fun insertItem(slot: Int, stack: ItemStack, simulate: Boolean) = delegate.insertItem(slot, stack, simulate)
    override fun extractItem(slot: Int, amount: Int, simulate: Boolean) = delegate.extractItem(slot, amount, simulate)
    override fun getSlotLimit(slot: Int) = delegate.getSlotLimit(slot)
    override fun isItemValid(slot: Int, stack: ItemStack) = delegate.isItemValid(slot, stack)
    override fun setStackInSlot(slot: Int, stack: ItemStack) = delegate.setStackInSlot(slot, stack)
}