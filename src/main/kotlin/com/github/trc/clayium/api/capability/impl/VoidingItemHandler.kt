package com.github.trc.clayium.api.capability.impl

import net.minecraft.item.ItemStack
import net.minecraftforge.items.IItemHandlerModifiable

open class VoidingItemHandler : IItemHandlerModifiable {
    override fun getSlots() = 1
    override fun getStackInSlot(slot: Int) = ItemStack.EMPTY
    override fun insertItem(slot: Int, stack: ItemStack, simulate: Boolean) = ItemStack.EMPTY
    override fun extractItem(slot: Int, amount: Int, simulate: Boolean) = ItemStack.EMPTY
    override fun getSlotLimit(slot: Int) = 64
    override fun setStackInSlot(slot: Int, stack: ItemStack) {}
}