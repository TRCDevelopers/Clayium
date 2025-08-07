package io.github.trcdevelopers.clayium.common.gui

import io.github.trcdevelopers.clayium.common.blocks.claycraftingtable.TileClayCraftingBoard
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.inventory.IInventory
import net.minecraft.inventory.Slot
import net.minecraft.item.ItemStack
import net.minecraft.util.EnumFacing
import net.minecraftforge.items.CapabilityItemHandler

class ContainerClayCraftingBoard(
    playerInv: IInventory,
    val tile: TileClayCraftingBoard,
) : ContainerClayium(playerInv, if (tile.neighborItemHandler.get() == null) 83 + 1 else 137 + 13 + 1) {

    val neighboringItemHandler = EnumFacing.entries.firstNotNullOfOrNull { facing ->
        tile.world.getTileEntity(tile.pos.offset(facing))
            ?.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, facing.opposite)
    }
    val hasNeighbor = neighboringItemHandler != null

    init {
    }

    override fun canInteractWith(playerIn: EntityPlayer): Boolean {
        return !playerIn.isSpectator
    }

    override fun transferStackInSlot(playerIn: EntityPlayer, index: Int): ItemStack {
        return ItemStack.EMPTY
    }

    companion object {
        private fun addPlayerSlots(container: ContainerClayCraftingBoard, playerInv: IInventory, tile : TileClayCraftingBoard) {
            // add player inventory slots
            for (i in 0..2) {
                for (j in 0..8) {
                    container.addSlotToContainer(Slot(playerInv, j + i * 9 + 9, 8 + j * 18, 84 + i * 18))
                }
            }
            // hot bar
            for (i in 0..8) {
                container.addSlotToContainer(Slot(playerInv, i, 8 + i * 18, 84 + 58))
            }
        }
    }
}