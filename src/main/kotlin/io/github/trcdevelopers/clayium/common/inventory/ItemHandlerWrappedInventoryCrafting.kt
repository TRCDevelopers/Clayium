package io.github.trcdevelopers.clayium.common.inventory

import net.minecraft.client.util.RecipeItemHelper
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.inventory.Container
import net.minecraft.inventory.InventoryCrafting
import net.minecraft.item.ItemStack
import net.minecraft.util.text.ITextComponent
import net.minecraft.util.text.TextComponentString
import net.minecraft.util.text.TextComponentTranslation
import net.minecraftforge.items.IItemHandler
import net.minecraftforge.items.IItemHandlerModifiable

/**
 * **Use [IItemHandler] whenever possible**.
 */
class ItemHandlerWrappedInventoryCrafting(
    val handler: IItemHandlerModifiable,
    eventHandler: Container,
    width: Int = 3,
    height: Int = 3,
) : InventoryCrafting(eventHandler, width, height) {

    override fun getSizeInventory(): Int {
        return handler.slots
    }

    override fun isEmpty(): Boolean {
        for (i in 0 until handler.slots) {
            if (!handler.getStackInSlot(i).isEmpty) {
                return false
            }
        }
        return true
    }

    override fun getStackInSlot(index: Int): ItemStack {
        if (index < 0 || index >= handler.slots) return ItemStack.EMPTY
        return handler.getStackInSlot(index)
    }

    override fun getStackInRowAndColumn(row: Int, column: Int): ItemStack {
        val index = row + column * this.width
        if (index < 0 || index >= handler.slots) return ItemStack.EMPTY
        return this.getStackInSlot(index)
    }

    override fun decrStackSize(index: Int, count: Int): ItemStack {
        if (index < 0 || index >= handler.slots) return ItemStack.EMPTY
        val stack = handler.extractItem(index, count, false)
        if (!stack.isEmpty) {
            eventHandler.onCraftMatrixChanged(this)
        }
        return stack
    }

    override fun removeStackFromSlot(index: Int): ItemStack {
        if (index < 0 || index >= handler.slots) return ItemStack.EMPTY
        return handler.extractItem(index, Int.MAX_VALUE, false)
    }

    override fun setInventorySlotContents(index: Int, stack: ItemStack) {
        if (index < 0 || index >= handler.slots) return
        handler.setStackInSlot(index, stack)
        eventHandler.onCraftMatrixChanged(this)
    }

    override fun getInventoryStackLimit(): Int {
        return 64
    }

    override fun markDirty() {}

    override fun isUsableByPlayer(player: EntityPlayer): Boolean {
        return true
    }

    override fun openInventory(player: EntityPlayer) {}
    override fun closeInventory(player: EntityPlayer) {}

    override fun isItemValidForSlot(index: Int, stack: ItemStack): Boolean {
        return false
    }

    override fun getField(id: Int): Int {
        return 0
    }

    override fun setField(id: Int, value: Int) {}

    override fun getFieldCount(): Int {
        return 0
    }

    override fun clear() {
        for (i in 0 until handler.slots) {
            handler.setStackInSlot(i, ItemStack.EMPTY)
        }
    }

    override fun getName(): String {
        return "clayium.container.item_handler_wrapped_inventory"
    }

    override fun hasCustomName(): Boolean {
        return false
    }

    override fun getDisplayName(): ITextComponent {
        return if (hasCustomName()) TextComponentString(name) else TextComponentTranslation(name)
    }

    override fun fillStackedContents(helper: RecipeItemHelper) {
        for (i in 0..<handler.slots) {
            val stack = handler.getStackInSlot(i)
            helper.accountStack(stack)
        }
    }
}