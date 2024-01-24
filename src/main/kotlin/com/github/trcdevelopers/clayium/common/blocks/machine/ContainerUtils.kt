package com.github.trcdevelopers.clayium.common.blocks.machine

import net.minecraft.inventory.IInventory
import net.minecraft.inventory.Slot

object ContainerUtils {
    fun getPlayerSlots(playerInv: IInventory, playerInvOffsetY: Int): List<Slot> {
        val slots = mutableListOf<Slot>()
        // hot bar
        for (i in 0..8) {
            slots.add(Slot(playerInv, i, 8 + i * 18, playerInvOffsetY + 58))
        }
        // add player inventory slots
        for (i in 0..2) {
            for (j in 0..8) {
                slots.add(Slot(playerInv, j + i * 9 + 9, 8 + j * 18, playerInvOffsetY + i * 18))
            }
        }
        return slots
    }
}