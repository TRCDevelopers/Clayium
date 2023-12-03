package com.github.trcdeveloppers.clayium.common.blocks.machine

import net.minecraft.entity.player.EntityPlayer
import net.minecraft.inventory.Container
import net.minecraft.inventory.IInventory
import net.minecraft.inventory.Slot
import net.minecraft.item.ItemStack

abstract class ContainerClayium(
    playerInv: IInventory,
    playerInvOffsetY: Int,
) : Container() {

    init {
        // add player inventory slots
        for (i in 0..2) {
            for (j in 0..8) {
                this.addSlotToContainer(Slot(playerInv, j + i * 9 + 9, 8 + j * 18, playerInvOffsetY + i * 18))
            }
        }
        // hot bar
        for (i in 0..8) {
            this.addSlotToContainer(Slot(playerInv, i, 8 + i * 18, playerInvOffsetY + 58))
        }
    }

    override fun canInteractWith(playerIn: EntityPlayer): Boolean {
        return !playerIn.isSpectator
    }

    override fun transferStackInSlot(playerIn: EntityPlayer, index: Int): ItemStack {
        var itemStack: ItemStack = ItemStack.EMPTY
        val slot: Slot? = inventorySlots[index]
        if (slot != null && slot.hasStack) {
            val itemStack1 = slot.stack
            itemStack = itemStack1.copy()
            val containerSlots = inventorySlots.size - playerIn.inventory.mainInventory.size
            if (index < containerSlots) {
                if (!mergeItemStack(itemStack1, containerSlots, inventorySlots.size, true)) {
                    return ItemStack.EMPTY
                }
            } else if (!mergeItemStack(itemStack1, 0, containerSlots, false)) {
                return ItemStack.EMPTY
            }
            if (itemStack1.count == 0) {
                slot.putStack(ItemStack.EMPTY)
            } else {
                slot.onSlotChanged()
            }
            if (itemStack1.count == itemStack.count) {
                return ItemStack.EMPTY
            }
            slot.onTake(playerIn, itemStack1)
        }
        return itemStack
    }
}