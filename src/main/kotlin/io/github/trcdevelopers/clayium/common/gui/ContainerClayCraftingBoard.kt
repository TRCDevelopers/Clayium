package io.github.trcdevelopers.clayium.common.gui

import io.github.trcdevelopers.clayium.common.blocks.claycraftingtable.TileClayCraftingBoard
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.inventory.Container
import net.minecraft.inventory.IInventory
import net.minecraft.inventory.Slot
import net.minecraft.item.ItemStack
import net.minecraft.util.EnumFacing
import net.minecraftforge.items.CapabilityItemHandler

class ContainerClayCraftingBoard(
    playerInv: IInventory,
    val tile: TileClayCraftingBoard,
) : Container() {

    val hasNeighbor: Boolean

    init {
        val neighboringItemHandler = EnumFacing.entries.firstNotNullOfOrNull { facing ->
            tile.world.getTileEntity(tile.pos.offset(facing))
                ?.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, facing.opposite)
        }
        if (neighboringItemHandler == null) {
            this.hasNeighbor = false
            // add player inventory slots
            for (i in 0..2) {
                for (j in 0..8) {
                    this.addSlotToContainer(Slot(playerInv, j + i * 9 + 9, 8 + j * 18, 84 + i * 18))
                }
            }
            // hot bar
            for (i in 0..8) {
                this.addSlotToContainer(Slot(playerInv, i, 8 + i * 18, 84 + 58))
            }
        } else {
            this.hasNeighbor = true
            // add player inventory slots
            for (i in 0..2) {
                for (j in 0..8) {
                    this.addSlotToContainer(Slot(playerInv, j + i * 9 + 9, 8 + j * 18, 84 + i * 18))
                }
            }
            // hot bar
            for (i in 0..8) {
                this.addSlotToContainer(Slot(playerInv, i, 8 + i * 18, 84 + 58))
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