package com.github.trcdeveloppers.clayium.common.blocks.machine.clayworktable

import net.minecraft.entity.player.EntityPlayer
import net.minecraft.inventory.Container
import net.minecraft.inventory.IContainerListener
import net.minecraft.inventory.IInventory
import net.minecraft.inventory.Slot
import net.minecraft.item.ItemStack
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import net.minecraftforge.items.CapabilityItemHandler
import net.minecraftforge.items.SlotItemHandler

class ClayWorktableContainer(playerInv: IInventory, private val tile: TileClayWorkTable) : Container() {
    private var lastCraftingProgress = 0
    private var lastRequiredProgress = 0

    init {
        val itemHandler = tile.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null)
        // Input
        addSlotToContainer(object : SlotItemHandler(itemHandler, 0, 17, 30) {
            override fun onSlotChanged() {
                tile.resetRecipeIfEmptyInput()
                tile.markDirty()
            }
        })
        // Tool
        addSlotToContainer(object : SlotItemHandler(itemHandler, 1, 80, 17) {
            override fun onSlotChanged() {
                tile.markDirty()
            }
        })
        // Primary Output
        addSlotToContainer(object : SlotItemHandler(itemHandler, 2, 143, 30) {
            override fun isItemValid(stack: ItemStack): Boolean {
                return false
            }

            override fun onSlotChanged() {
                tile.markDirty()
            }
        })
        // Secondary Output
        addSlotToContainer(object : SlotItemHandler(itemHandler, 3, 143, 55) {
            override fun isItemValid(stack: ItemStack): Boolean {
                return false
            }

            override fun onSlotChanged() {
                tile.markDirty()
            }
        })
        for (i in 0..2) {
            for (j in 0..8) {
                addSlotToContainer(Slot(playerInv, j + i * 9 + 9, 8 + j * 18, 84 + i * 18))
            }
        }
        for (i in 0..8) {
            addSlotToContainer(Slot(playerInv, i, 8 + i * 18, 142))
        }
    }

    override fun canInteractWith(playerIn: EntityPlayer): Boolean {
        return !playerIn.isSpectator
    }

    override fun transferStackInSlot(playerIn: EntityPlayer, index: Int): ItemStack {
        var itemstack = ItemStack.EMPTY
        val slot = inventorySlots[index]
        if (slot != null && slot.hasStack) {
            val itemstack1 = slot.stack
            itemstack = itemstack1.copy()
            val containerSlots = inventorySlots.size - playerIn.inventory.mainInventory.size
            if (index < containerSlots) {
                if (!mergeItemStack(itemstack1, containerSlots, inventorySlots.size, true)) {
                    return ItemStack.EMPTY
                }
            } else if (!mergeItemStack(itemstack1, 0, containerSlots, false)) {
                return ItemStack.EMPTY
            }
            if (itemstack1.count == 0) {
                slot.putStack(ItemStack.EMPTY)
            } else {
                slot.onSlotChanged()
            }
            if (itemstack1.count == itemstack.count) {
                return ItemStack.EMPTY
            }
            slot.onTake(playerIn, itemstack1)
        }
        return itemstack
    }

    override fun addListener(listener: IContainerListener) {
        super.addListener(listener)
        listener.sendWindowProperty(this, 0, tile.craftingProgress)
        listener.sendWindowProperty(this, 1, tile.requiredProgress)
    }

    override fun detectAndSendChanges() {
        super.detectAndSendChanges()
        for (listener in listeners) {
            if (lastCraftingProgress != tile.craftingProgress) {
                listener.sendWindowProperty(this, 0, tile.craftingProgress)
            }
            if (lastRequiredProgress != tile.requiredProgress) {
                listener.sendWindowProperty(this, 1, tile.requiredProgress)
            }
        }
        lastCraftingProgress = tile.craftingProgress
        lastRequiredProgress = tile.requiredProgress
    }

    @SideOnly(Side.CLIENT)
    override fun updateProgressBar(id: Int, data: Int) {
        when (id) {
            0 -> tile.craftingProgress = data
            1 -> tile.requiredProgress = data
        }
    }

    override fun enchantItem(playerIn: EntityPlayer, id: Int): Boolean {
        tile.pushButton(id)
        return true
    }
}
