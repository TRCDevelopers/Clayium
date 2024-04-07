package com.github.trcdevelopers.clayium.common.gui

import com.github.trcdevelopers.clayium.common.blocks.clayworktable.TileClayWorkTable
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.inventory.IContainerListener
import net.minecraft.inventory.IInventory
import net.minecraft.item.ItemStack
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import net.minecraftforge.items.CapabilityItemHandler
import net.minecraftforge.items.SlotItemHandler

class ContainerClayWorkTable(
    playerInv: IInventory,
    private val tile: TileClayWorkTable,
) : ContainerClayium(playerInv, 84) {
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
    }

    override fun canInteractWith(playerIn: EntityPlayer): Boolean {
        return !playerIn.isSpectator
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
