package com.github.trcdeveloppers.clayium.common.blocks.machine.claybuffer

import com.github.trcdeveloppers.clayium.common.blocks.machine.ContainerClayium
import net.minecraft.inventory.IInventory
import net.minecraftforge.items.CapabilityItemHandler
import net.minecraftforge.items.SlotItemHandler

class ContainerClayBuffer(
    tier: Int,
    playerInv: IInventory,
    private val tile: TileClayBuffer,
) : ContainerClayium(playerInv, PLAYER_INV_OFFSET_Y) {

    init {
        val machineGuiSizeX = 176
        val machineGuiSizeY = 72
        val itemHandler = tile.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null)
            ?: throw IllegalStateException("TileEntity does not capable of ITEM_HANDLER_CAPABILITY")
        for (j in 0..<tile.inventoryY) {
            for (i in 0..<tile.inventoryX) {
                this.addSlotToContainer(SlotItemHandler(itemHandler, i + j * tile.inventoryX, i * 18 + (machineGuiSizeX - 18 * tile.inventoryX) / 2, j * 18 + 18))
            }
        }
    }

    companion object {
        const val PLAYER_INV_OFFSET_Y: Int = 48
    }
}