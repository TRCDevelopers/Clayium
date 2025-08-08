package io.github.trcdevelopers.clayium.common.gui

import io.github.trcdevelopers.clayium.common.blocks.claycraftingtable.TileClayCraftingBoard
import io.github.trcdevelopers.clayium.common.inventory.ItemHandlerWrappedInventoryCrafting
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.inventory.IInventory
import net.minecraft.inventory.InventoryCraftResult
import net.minecraft.inventory.Slot
import net.minecraft.inventory.SlotCrafting
import net.minecraft.item.ItemStack
import net.minecraft.util.EnumFacing
import net.minecraft.world.World
import net.minecraftforge.items.CapabilityItemHandler
import net.minecraftforge.items.SlotItemHandler

class ContainerClayCraftingBoard(
    private val player: EntityPlayer,
    private val world: World,
    val tile: TileClayCraftingBoard,
) : ContainerClayium() {

    val neighboringItemHandler = EnumFacing.entries.firstNotNullOfOrNull { facing ->
        tile.world.getTileEntity(tile.pos.offset(facing))
            ?.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, facing.opposite)
    }
    val hasNeighbor = neighboringItemHandler != null

    private val craftMatrix = ItemHandlerWrappedInventoryCrafting(tile.inventory, this, 3, 3)
    private val craftResult = InventoryCraftResult()

    init {
        // SlotCrafting must be added first because [Container.onCraftMatrixChanged] will use hardcoded index 0 for result slot
        val slotCrafting = SlotCrafting(player, this.craftMatrix, this.craftResult, 0, 124 + 1, 35 + 1)
        this.addSlotToContainer(slotCrafting)

        val neighborSlotsY = 75
        addPlayerSlots(this, player.inventory, if (hasNeighbor) 75 + 18*3 + 13 + 1 else 83 + 1)
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

        for (i in 0..<3) {
            for (j in 0..<3) {
                val slotIndex = i * 3 + j
                val slotX = (j * 18) + 29 + 1
                val slotY = (i * 18) + 16 + 1
                val slot = Slot(this.craftMatrix, slotIndex, slotX, slotY)
                this.addSlotToContainer(slot)
            }
        }
    }

    override fun canInteractWith(playerIn: EntityPlayer): Boolean {
        return !playerIn.isSpectator
    }

    override fun transferStackInSlot(playerIn: EntityPlayer, index: Int): ItemStack {
        return ItemStack.EMPTY
    }

    override fun onCraftMatrixChanged(inventoryIn: IInventory) {
        this.slotChangedCraftingGrid(this.world, this.player, this.craftMatrix, this.craftResult)
    }

    override fun canMergeSlot(stack: ItemStack, slotIn: Slot): Boolean {
        return slotIn.inventory != this.craftResult && super.canMergeSlot(stack, slotIn)
    }
}