package com.github.trcdevelopers.clayium.common.blocks

import com.github.trcdevelopers.clayium.common.blocks.machine.ContainerUtils
import com.github.trcdevelopers.clayium.common.blocks.machine.claybuffer.TileClayBuffer
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.inventory.Container
import net.minecraft.inventory.IInventory
import net.minecraft.item.ItemStack
import net.minecraftforge.items.CapabilityItemHandler
import net.minecraftforge.items.SlotItemHandler

class GuiContainerClayBuffer(
    playerInv: IInventory,
    tile: TileEntityClayBuffer,
) : Container() {
    init {
        ContainerUtils.getPlayerSlots(playerInv, 0, 18 + tile.inventoryY * 18 + 12).forEach(this::addSlotToContainer)
        val machineGuiSizeX = 176
        val machineGuiSizeY = 72
        val itemHandler = tile.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null)
        if (itemHandler != null) {
            for (j in 0..<tile.inventoryY) {
                for (i in 0..<tile.inventoryX) {
                    this.addSlotToContainer(SlotItemHandler(itemHandler, i + j * tile.inventoryX, i * 18 + (machineGuiSizeX - 18 * tile.inventoryX) / 2, j * 18 + 18))
                }
            }
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
            // hot bar -> container? -> player inventory
            in 0..<9 -> {
                if (!(mergeItemStack(slotStack, 36, inventorySlots.size, false)
                            || mergeItemStack(slotStack, 9, 35, false))) {
                    return ItemStack.EMPTY
                }
            }
            // player inventory -> container? -> hot bar
            in 9..<36 -> {
                if (!(mergeItemStack(slotStack, 36, inventorySlots.size, false)
                            || mergeItemStack(slotStack, 0, 8, false))) {
                    return ItemStack.EMPTY
                }
            }
            // container -> hot bar? -> player inventory
            in 36..<inventorySlots.size -> {
                if (!mergeItemStack(slotStack, 0, 35, false)) {
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