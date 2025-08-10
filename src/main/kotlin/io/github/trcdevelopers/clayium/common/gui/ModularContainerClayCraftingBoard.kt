
package io.github.trcdevelopers.clayium.common.gui

import com.cleanroommc.modularui.screen.ModularContainer
import com.cleanroommc.modularui.test.CraftingModularContainer
import com.cleanroommc.modularui.widgets.slot.InventoryCraftingWrapper
import com.cleanroommc.modularui.widgets.slot.ModularCraftingSlot
import com.cleanroommc.modularui.widgets.slot.ModularSlot
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.inventory.IInventory
import net.minecraft.inventory.InventoryCraftResult
import net.minecraft.inventory.InventoryCrafting
import net.minecraft.item.ItemStack
import net.minecraft.item.crafting.IRecipe
import net.minecraft.world.World
import net.minecraftforge.items.IItemHandlerModifiable

/**
 * [CraftingModularContainer] that saves last recipe used, so no downtime after crafting.
 * @see CraftingModularContainer
 */
class ModularContainerClayCraftingBoard(
    width: Int, height: Int, handler: IItemHandlerModifiable,
    startIndex: Int = 0,
) : ModularContainer() {

    private val craftMatrix = InventoryCraftingWrapper(this, width, height, handler, startIndex)
    private lateinit var resultSlot: ModularCraftingSlot

    private var lastRecipeUsed: IRecipe? = null

    override fun detectAndSendChanges() {
        super.detectAndSendChanges()
        this.craftMatrix.detectChanges()
    }

    override fun onCraftMatrixChanged(inventoryIn: IInventory) {}

    override fun slotChangedCraftingGrid(world: World, entityPlayer: EntityPlayer, inventoryCrafting: InventoryCrafting, inventoryCraftResult: InventoryCraftResult) {
        val lastRecipe = this.lastRecipeUsed
        var result: ItemStack = ItemStack.EMPTY
        if (lastRecipe != null && lastRecipe.matches(inventoryCrafting, world)) {
            result = lastRecipe.getCraftingResult(inventoryCrafting)
        }
        if (result.isEmpty) {
            // invalidate the lastRecipeUsed and sync to clients that opening this container
        } else {
            this.lastRecipeUsed = lastRecipe
            resultSlot.setRecipeUsed(lastRecipe)
            resultSlot.updateResult(result)
        }
    }

    @Suppress("UnstableApiUsage")
    override fun registerSlot(panelName: String?, slot: ModularSlot?) {
        super.registerSlot(panelName, slot)
        if (slot is ModularCraftingSlot) {
            if (::resultSlot.isInitialized && resultSlot == slot)  {
                throw IllegalArgumentException("Only one crafting output slot is supported with ModularContainerClayCraftingBoard!")
            }
            this.resultSlot = slot
            slot.setCraftMatrix(this.craftMatrix)
        }
    }
}