package com.github.trcdevelopers.clayium.common.gui

import com.github.trcdevelopers.clayium.common.blocks.machine.ContainerClayium
import com.github.trcdevelopers.clayium.common.blocks.machine.tile.TileSingle2SingleMachine
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.inventory.IInventory
import net.minecraft.item.ItemStack
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import net.minecraftforge.items.SlotItemHandler

class ContainerSingle2SingleMachine(
    playerInv: IInventory,
    private val tile: TileSingle2SingleMachine,
) : ContainerClayium(playerInv, 84) {
    private var lastCraftingProgress = 0
    private var lastRequiredProgress = 0

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
        addSlotToContainer(object : SlotItemHandler(tile.ceSlot, 0, 160, 60) {
            override fun isItemValid(stack: ItemStack) = false
            override fun canTakeStack(playerIn: EntityPlayer) = false
        })
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
}