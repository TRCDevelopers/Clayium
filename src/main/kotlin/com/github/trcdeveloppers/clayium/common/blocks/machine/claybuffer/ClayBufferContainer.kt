package com.github.trcdeveloppers.clayium.common.blocks.machine.claybuffer

import com.github.trcdeveloppers.clayium.common.blocks.machine.ContainerClayium
import net.minecraft.inventory.IInventory
import net.minecraft.inventory.Slot
import net.minecraftforge.items.CapabilityItemHandler
import net.minecraftforge.items.IItemHandler
import net.minecraftforge.items.SlotItemHandler

class ClayBufferContainer(
    tier: Int,
    playerInv: IInventory,
    private val tile: TileClayBuffer,
) : ContainerClayium(playerInv) {

    init {
        val itemHandler = tile.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null)
            ?: throw IllegalStateException("TileEntity does not capable of ITEM_HANDLER_CAPABILITY")
        getSlotsFromTier(tier, itemHandler).forEach { addSlotToContainer(it) }
    }

    companion object {
        fun getSlotsFromTier(tier: Int, handler: IItemHandler): List<Slot> {
            val slots = mutableListOf<Slot>()
            when (tier) {
                4 -> for (i in 0..2) slots.add(SlotItemHandler(handler, i, 17 + i * 18, 17))
                //todo: 5 - 13
                in 5..13 -> slots.add(SlotItemHandler(handler, 0, 17, 17))
                else -> throw IllegalArgumentException("Invalid tier: $tier")
            }
            return slots
        }
    }
}