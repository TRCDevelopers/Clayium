package com.github.trcdevelopers.clayium.common.blocks.machine.single_single

import com.github.trcdevelopers.clayium.common.blocks.machine.ContainerClayium
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.inventory.IInventory
import net.minecraft.item.ItemStack
import net.minecraftforge.items.CapabilityItemHandler
import net.minecraftforge.items.SlotItemHandler

class ContainerSingleSingle(
    playerInv: IInventory,
    private val tile: TileSingleSingle,
) : ContainerClayium(playerInv, 84) {
    init {
        val itemHandler = tile.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null) ?: throw NullPointerException("Item handler capability is null.")

        addSlotToContainer(SlotItemHandler(itemHandler, 0, 44, 35))
        addSlotToContainer(SlotItemHandler(itemHandler, 1, 116, 35))
    }

    override fun transferStackInSlot(playerIn: EntityPlayer, index: Int): ItemStack {
        println("clicked: $index")
        val slot = inventorySlots[index]
        if (slot == null || !slot.hasStack) return ItemStack.EMPTY
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
                if (!mergeItemStack(slotStack, 0, 35, true)) {
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