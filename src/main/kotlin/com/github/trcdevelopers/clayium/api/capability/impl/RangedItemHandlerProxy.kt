package com.github.trcdevelopers.clayium.api.capability.impl

import net.minecraft.item.ItemStack
import net.minecraftforge.items.IItemHandler

class RangedItemHandlerProxy(
    private val itemHandler: IItemHandler,
    private val range: IntRange,
) : IItemHandler {

    constructor(itemHandler: IItemHandler, availableSlot: Int) : this(itemHandler, availableSlot..availableSlot)

    override fun getSlots(): Int {
        return range.count()
    }

    override fun getStackInSlot(slot: Int): ItemStack {
        return if (slot in range) {
            itemHandler.getStackInSlot(slot)
        } else {
            ItemStack.EMPTY
        }
    }

    override fun insertItem(slot: Int, stack: ItemStack, simulate: Boolean): ItemStack {
        return if (slot in range) {
            itemHandler.insertItem(slot, stack, simulate)
        } else {
            stack
        }
    }

    override fun extractItem(slot: Int, amount: Int, simulate: Boolean): ItemStack {
        return if (slot in range) {
            itemHandler.extractItem(slot, amount, simulate)
        } else {
            ItemStack.EMPTY
        }
    }

    override fun getSlotLimit(slot: Int): Int {
        return itemHandler.getSlotLimit(slot)
    }
}