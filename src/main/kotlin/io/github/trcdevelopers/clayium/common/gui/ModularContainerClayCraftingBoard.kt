
package io.github.trcdevelopers.clayium.common.gui

import com.cleanroommc.modularui.screen.ModularContainer
import com.cleanroommc.modularui.test.CraftingModularContainer
import com.cleanroommc.modularui.widgets.slot.InventoryCraftingWrapper
import com.cleanroommc.modularui.widgets.slot.ModularCraftingSlot
import com.cleanroommc.modularui.widgets.slot.ModularSlot
import io.github.trcdevelopers.clayium.common.blocks.claycraftingtable.TileClayCraftingBoard
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.inventory.IInventory
import net.minecraft.inventory.InventoryCraftResult
import net.minecraft.inventory.InventoryCrafting
import net.minecraft.item.ItemStack
import net.minecraft.world.World
import net.minecraftforge.items.IItemHandlerModifiable

/**
 * [CraftingModularContainer] that saves last recipe used, so no downtime after crafting.
 * @see CraftingModularContainer
 */
class ModularContainerClayCraftingBoard(
    val tile: TileClayCraftingBoard,
    width: Int, height: Int, handler: IItemHandlerModifiable,
    startIndex: Int = 0,
) : ModularContainer() {

    private val craftMatrix = InventoryCraftingWrapper(this, width, height, handler, startIndex)
    private lateinit var resultSlot: ModularCraftingSlot

    override fun detectAndSendChanges() {
        super.detectAndSendChanges()
        this.craftMatrix.detectChanges()
    }

    override fun onCraftMatrixChanged(inventoryIn: IInventory) {
        if (guiData.isClient) return
        val recipe = tile.currentRecipe
        val resultStack: ItemStack = recipe?.getCraftingResult(this.craftMatrix) ?: ItemStack.EMPTY
        if (!resultStack.isEmpty) {
            this.resultSlot.setRecipeUsed(recipe)
        }
        this.resultSlot.updateResult(resultStack)
    }

    override fun slotChangedCraftingGrid(world: World, entityPlayer: EntityPlayer, inventoryCrafting: InventoryCrafting, inventoryCraftResult: InventoryCraftResult) {
        this.onCraftMatrixChanged(inventoryCrafting)
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