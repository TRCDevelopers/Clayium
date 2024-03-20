package com.github.trcdevelopers.clayium.common.gui

import com.github.trcdevelopers.clayium.common.blocks.machine.ContainerClayium
import com.github.trcdevelopers.clayium.common.blocks.machine.tile.TileSingle2SingleMachine
import net.minecraft.inventory.IInventory
import net.minecraft.item.ItemStack
import net.minecraftforge.items.SlotItemHandler

class ContainerSingle2SingleMachine(
    playerInv: IInventory,
    private val tile: TileSingle2SingleMachine,
) : ContainerClayium(playerInv, 84) {
    init {
        val itemHandler = tile.getItemHandler()
        machineInventorySlots.add(
            addSlotToContainer(SlotItemHandler(itemHandler, 0, 44, 35))
        )
        machineInventorySlots.add(
            addSlotToContainer(object : SlotItemHandler(itemHandler, 1, 116, 35) {
                override fun isItemValid(stack: ItemStack) = false
            })
        )
    }
}