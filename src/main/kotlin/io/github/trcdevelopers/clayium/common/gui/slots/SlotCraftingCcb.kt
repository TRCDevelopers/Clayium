package io.github.trcdevelopers.clayium.common.gui.slots

import io.github.trcdevelopers.clayium.api.capability.impl.EmptyItemStackHandler
import io.github.trcdevelopers.clayium.api.util.toList
import io.github.trcdevelopers.clayium.common.gui.ContainerClayCraftingBoard
import io.github.trcdevelopers.clayium.common.inventory.ItemHandlerWrappedInventoryCrafting
import io.github.trcdevelopers.clayium.common.util.isIdenticalTo
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.inventory.IInventory
import net.minecraft.inventory.SlotCrafting
import net.minecraft.item.ItemStack
import net.minecraft.item.crafting.CraftingManager
import net.minecraftforge.common.ForgeHooks
import net.minecraftforge.items.IItemHandler
import net.minecraftforge.items.ItemHandlerHelper

/**
 * SlotCrafting for Clay Crafting Board (CCB).
 *
 * This tries to consume items in the neighboring inventory first, and then crafting grid items.
 */
class SlotCraftingCcb(
    private val player: EntityPlayer,
    private val craftMatrix: ItemHandlerWrappedInventoryCrafting,
    craftResult: IInventory,
    slotIndex: Int,
    x: Int,
    y: Int,
    private val neighboringInventory: IItemHandler = EmptyItemStackHandler,
    private val eventHandler: ContainerClayCraftingBoard,
) : SlotCrafting(player, craftMatrix, craftResult, slotIndex, x, y) {

    override fun onTake(thePlayer: EntityPlayer, stack: ItemStack): ItemStack {
        this.onCrafting(stack)
        ForgeHooks.setCraftingPlayer(thePlayer)
        val remainingItemStacks = CraftingManager.getRemainingItems(this.craftMatrix, thePlayer.world)
        ForgeHooks.setCraftingPlayer(null)

        for (remainingStack in remainingItemStacks) {
            val remain = ItemHandlerHelper.insertItem(this.neighboringInventory, remainingStack, false)
            this.player.inventory.addItemStackToInventory(remain)
            this.player.dropItem(remain, false)
        }

        matrix@ for ((craftGridIndex, craftGridStack) in this.craftMatrix.handler.toList().withIndex()) {
            // neighbor inv first
            for (neighborIndex in 0..<this.neighboringInventory.slots) {
                val s = this.neighboringInventory.getStackInSlot(neighborIndex)
                if (s.isEmpty) continue
                if (s.isIdenticalTo(craftGridStack)) {
                    this.neighboringInventory.extractItem(neighborIndex, 1, false)
                    continue@matrix
                }
            }
            // if not found in neighbor, consume from crafting grid
            this.craftMatrix.decrStackSize(craftGridIndex, 1)
        }
        this.eventHandler.onCraftMatrixChanged(this.craftMatrix)
        return stack
    }
}