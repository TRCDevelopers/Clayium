package com.github.trcdevelopers.clayium.common.blocks.machine.claybuffer

import com.github.trcdevelopers.clayium.common.blocks.machine.ContainerClayium
import com.github.trcdevelopers.clayium.common.blocks.machine.tile.TileClayBuffer
import net.minecraft.inventory.IInventory
import net.minecraftforge.items.CapabilityItemHandler
import net.minecraftforge.items.SlotItemHandler

class ContainerClayBuffer(
    playerInv: IInventory,
    tile: TileClayBuffer,
) : ContainerClayium(playerInv, 30 + tile.inventoryY * 18) {
    init {
        val machineGuiSizeX = 176
        val itemHandler = tile.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null)
        for (j in 0..<tile.inventoryY) {
            for (i in 0..<tile.inventoryX) {
                this.addSlotToContainer(SlotItemHandler(itemHandler, i + j * tile.inventoryX, i * 18 + (machineGuiSizeX - 18 * tile.inventoryX) / 2, j * 18 + 18))
            }
        }
    }
}