package io.github.trcdevelopers.clayium.common.gui.slots

import com.google.common.collect.Lists
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.inventory.InventoryCraftResult
import net.minecraft.inventory.InventoryCrafting
import net.minecraft.inventory.Slot
import net.minecraft.item.ItemStack
import net.minecraft.item.crafting.CraftingManager
import net.minecraftforge.common.ForgeHooks
import kotlin.math.min

class SlotCraftingClayium(
    private val player: EntityPlayer,
    private val craftMatrix: InventoryCrafting,
    private val inventoryCraftResult: InventoryCraftResult,
    slotIndex: Int,
    xPos: Int,
    yPos: Int,
) : Slot(inventoryCraftResult, slotIndex, xPos, yPos) {

    private var amountCrafted = 0

    override fun isItemValid(stack: ItemStack): Boolean {
        return false // This slot is output only
    }

    override fun decrStackSize(amount: Int): ItemStack {
        if (this.hasStack) {
            this.amountCrafted += min(amount, this.stack.count)
        }
        return super.decrStackSize(amount)
    }

    override fun onCrafting(stack: ItemStack, amount: Int) {
        this.amountCrafted += amount
        this.onCrafting(stack)
    }

    override fun onSwapCraft(amount: Int) {
        this.amountCrafted += amount
    }

    override fun onCrafting(stack: ItemStack) {
        if (this.amountCrafted > 0) {
            stack.onCrafting(this.player.world, this.player, this.amountCrafted)
            net.minecraftforge.fml.common.FMLCommonHandler.instance()
                .firePlayerCraftingEvent(this.player, stack, craftMatrix)
        }

        this.amountCrafted = 0;
        val recipe = inventoryCraftResult.recipeUsed;

        if (recipe != null && !recipe.isDynamic) {
            this.player.unlockRecipes(Lists.newArrayList(recipe));
            inventoryCraftResult.recipeUsed = null;
        }
    }

    override fun onTake(thePlayer: EntityPlayer, stack: ItemStack): ItemStack {
        this.onCrafting(stack)
        ForgeHooks.setCraftingPlayer(thePlayer)
        val nonnulllist = CraftingManager.getRemainingItems(this.craftMatrix, thePlayer.world)
        ForgeHooks.setCraftingPlayer(null)

        for (i in nonnulllist.indices) {
            var itemstack = this.craftMatrix.getStackInSlot(i)
            val itemstack1 = nonnulllist[i]

            if (!itemstack.isEmpty) {
                this.craftMatrix.decrStackSize(i, 1)
                itemstack = this.craftMatrix.getStackInSlot(i)
            }

            if (!itemstack1.isEmpty) {
                if (itemstack.isEmpty) {
                    this.craftMatrix.setInventorySlotContents(i, itemstack1)
                } else if (ItemStack.areItemsEqual(itemstack, itemstack1) && ItemStack.areItemStackTagsEqual(itemstack,
                        itemstack1)
                ) {
                    itemstack1.grow(itemstack.count)
                    this.craftMatrix.setInventorySlotContents(i, itemstack1)
                } else if (!this.player.inventory.addItemStackToInventory(itemstack1)) {
                    this.player.dropItem(itemstack1, false)
                }
            }
        }

        return stack
    }
}