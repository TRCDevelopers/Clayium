package com.github.trcdevelopers.clayium.common.gui

import net.minecraft.entity.player.EntityPlayer
import net.minecraft.inventory.Container
import net.minecraft.inventory.IInventory
import net.minecraft.inventory.Slot
import net.minecraft.item.ItemStack

abstract class ContainerClayium(
    playerInv: IInventory,
    playerInvOffsetY: Int,
) : Container() {

    protected val machineInventorySlots = arrayListOf<Slot>()

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
        val slot = inventorySlots[index] ?: return ItemStack.EMPTY
        if (!slot.hasStack) return ItemStack.EMPTY
        val slotStack = slot.stack
        val orgStack = slotStack.copy()

        when (index) {
            // player inventory -> container? -> hot bar
            in 0..<27 -> {
                if (!(mergeItemStack(slotStack, 36, inventorySlots.size, false)
                            || mergeItemStack(slotStack, 27, 36, false))) {
                    return ItemStack.EMPTY
                }
            }
            // hot bar -> container? -> player inventory
            in 27..<36 -> {
                if (!(mergeItemStack(slotStack, 36, inventorySlots.size, false)
                            || mergeItemStack(slotStack, 0, 27, false))) {
                    return ItemStack.EMPTY
                }
            }
            // container -> hot bar? -> player inventory
            in 36..<inventorySlots.size -> {
                if (!(mergeItemStack(slotStack, 27, 36, false)
                        || mergeItemStack(slotStack, 0, 27, false))) {
                    return ItemStack.EMPTY
                }
            }
        }

        if (slotStack.isEmpty) {
            slot.putStack(ItemStack.EMPTY)
        } else {
            slot.onSlotChanged()
        }

        // no changes were made
        if (slotStack.count == orgStack.count) {
            return ItemStack.EMPTY
        }
        slot.onTake(playerIn, slotStack)
        return orgStack
    }
}