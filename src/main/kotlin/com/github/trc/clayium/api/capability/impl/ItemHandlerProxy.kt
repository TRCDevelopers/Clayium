package com.github.trc.clayium.api.capability.impl

import net.minecraft.item.ItemStack
import net.minecraftforge.items.IItemHandler

class ItemHandlerProxy(
    inputInventory: IItemHandler?,
    outputInventory: IItemHandler?,
) : IItemHandler {

    private val inputInventory: IItemHandler = inputInventory ?: EmptyItemStackHandler
    private val outputInventory: IItemHandler = outputInventory ?: EmptyItemStackHandler

    override fun getSlots(): Int {
        return inputInventory.slots + outputInventory.slots
    }

    override fun getStackInSlot(slot: Int): ItemStack {
        return if (slot < inputInventory.slots) {
            inputInventory.getStackInSlot(slot)
        } else {
            outputInventory.getStackInSlot(slot - inputInventory.slots)
        }
    }

    override fun insertItem(slot: Int, stack: ItemStack, simulate: Boolean): ItemStack {
        return if (slot < inputInventory.slots) {
            inputInventory.insertItem(slot, stack, simulate)
        } else {
            stack
        }
    }

    override fun extractItem(slot: Int, amount: Int, simulate: Boolean): ItemStack {
        return if (slot < inputInventory.slots) {
            ItemStack.EMPTY
        } else {
            outputInventory.extractItem(slot - inputInventory.slots, amount, simulate)
        }
    }

    override fun getSlotLimit(slot: Int): Int {
        return if (slot < inputInventory.slots) {
            inputInventory.getSlotLimit(slot)
        } else {
            outputInventory.getSlotLimit(slot - inputInventory.slots)
        }
    }
}