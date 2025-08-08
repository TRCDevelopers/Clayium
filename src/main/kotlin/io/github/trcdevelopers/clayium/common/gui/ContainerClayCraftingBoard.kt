package io.github.trcdevelopers.clayium.common.gui

import io.github.trcdevelopers.clayium.common.blocks.claycraftingtable.TileClayCraftingBoard
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.inventory.IInventory
import net.minecraft.item.ItemStack
import net.minecraft.util.EnumFacing
import net.minecraftforge.items.CapabilityItemHandler
import net.minecraftforge.items.SlotItemHandler

class ContainerClayCraftingBoard(
    playerInv: IInventory,
    val tile: TileClayCraftingBoard,
) : ContainerClayium(playerInv) {

    val neighboringItemHandler = EnumFacing.entries.firstNotNullOfOrNull { facing ->
        tile.world.getTileEntity(tile.pos.offset(facing))
            ?.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, facing.opposite)
    }
    val hasNeighbor = neighboringItemHandler != null

    init {
        val neighborSlotsY = 75
        addPlayerSlots(this, playerInv, if (hasNeighbor) 75 + 18*3 + 13 + 1 else 83 + 1)
        if (this.neighboringItemHandler != null) {
            val rowSize = this.neighboringItemHandler.slots / 9

            for (i in 0..<rowSize) {
                if (i > 3) break
                for (j in 0..<9) {
                    val slotIndex = i * 9 + j
                    if (slotIndex >= this.neighboringItemHandler.slots) break
                    val slotX = (j * 18) + 7 + 1
                    val slotY = (i * 18) + neighborSlotsY + 1
                    val slot = SlotItemHandler(this.neighboringItemHandler, slotIndex, slotX, slotY)
                    this.addSlotToContainer(slot)
                }
            }
        }
    }

    override fun canInteractWith(playerIn: EntityPlayer): Boolean {
        return !playerIn.isSpectator
    }

    override fun transferStackInSlot(playerIn: EntityPlayer, index: Int): ItemStack {
        return ItemStack.EMPTY
    }
}