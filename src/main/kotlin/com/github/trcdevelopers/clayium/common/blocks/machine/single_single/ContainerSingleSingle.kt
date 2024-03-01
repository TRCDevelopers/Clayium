package com.github.trcdevelopers.clayium.common.blocks.machine.single_single

import com.github.trcdevelopers.clayium.common.blocks.machine.ContainerClayium
import net.minecraft.inventory.IInventory
import net.minecraftforge.items.CapabilityItemHandler
import net.minecraftforge.items.SlotItemHandler

class ContainerSingleSingle(
    playerInv: IInventory,
    private val tile: TileSingleSingle,
) : ContainerClayium(playerInv, 84) {
    init {
        val itemHandler = tile.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null) ?: throw NullPointerException("Item handler capability is null.")

        addSlotToContainer(object : SlotItemHandler(itemHandler, 0, 44, 35) {
            override fun onSlotChanged() {
                tile.markDirty()
            }
        })
        addSlotToContainer(object : SlotItemHandler(itemHandler, 1, 116, 35) {
            override fun onSlotChanged() {
                tile.markDirty()
            }
        })
    }
}