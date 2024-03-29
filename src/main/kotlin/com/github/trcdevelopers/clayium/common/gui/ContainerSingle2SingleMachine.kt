package com.github.trcdevelopers.clayium.common.gui

import com.github.trcdevelopers.clayium.common.blocks.machine.ContainerClayium
import com.github.trcdevelopers.clayium.common.blocks.machine.tile.TileSingle2SingleMachine
import com.github.trcdevelopers.clayium.common.clayenergy.ClayEnergy
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.inventory.IInventory
import net.minecraft.item.ItemStack
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import net.minecraftforge.items.SlotItemHandler
import java.nio.ByteBuffer

class ContainerSingle2SingleMachine(
    playerInv: IInventory,
    private val tile: TileSingle2SingleMachine,
) : ContainerClayium(playerInv, 84) {
    private var lastCraftingProgress = -1
    private var lastRequiredProgress = -1
    private var lastCe1 = -1
    private var lastCe2 = -1

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
        addSlotToContainer(object : SlotItemHandler(tile.ceSlot, 0, 155, 59) {
            override fun isItemValid(stack: ItemStack) = false
            override fun canTakeStack(playerIn: EntityPlayer) = false
        })
    }

    override fun detectAndSendChanges() {
        super.detectAndSendChanges()

        val buf = ByteBuffer.allocate(Long.SIZE_BYTES).putLong(tile.storedCe.energy)
        val ce1 = buf.getInt(0)
        val ce2 = buf.getInt(4)

        for (listener in listeners) {
            if (lastCraftingProgress != tile.craftingProgress) {
                listener.sendWindowProperty(this, 0, tile.craftingProgress)
            }
            if (lastRequiredProgress != tile.requiredProgress) {
                listener.sendWindowProperty(this, 1, tile.requiredProgress)
            }
            if (lastCe1 != ce1) {
                listener.sendWindowProperty(this, 2, ce1)
            }
            if (lastCe2 != ce2) {
                listener.sendWindowProperty(this, 3, ce2)
            }
        }
        lastCraftingProgress = tile.craftingProgress
        lastRequiredProgress = tile.requiredProgress
        lastCe1 = ce1
        lastCe2 = ce2
    }

    @SideOnly(Side.CLIENT)
    override fun updateProgressBar(id: Int, data: Int) {
        when (id) {
            0 -> tile.craftingProgress = data
            1 -> tile.requiredProgress = data
            2 -> tile.storedCe = ClayEnergy(ByteBuffer.allocate(Long.SIZE_BYTES).putLong(0, tile.storedCe.energy).putInt(0, data).getLong(0))
            3 -> tile.storedCe = ClayEnergy(ByteBuffer.allocate(Long.SIZE_BYTES).putLong(0, tile.storedCe.energy).putInt(4, data).getLong(0))
        }
    }
}