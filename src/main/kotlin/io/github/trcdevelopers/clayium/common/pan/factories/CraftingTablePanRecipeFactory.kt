package io.github.trcdevelopers.clayium.common.pan.factories

import io.github.trcdevelopers.clayium.api.ClayEnergy
import io.github.trcdevelopers.clayium.api.pan.IPanRecipe
import io.github.trcdevelopers.clayium.api.pan.IPanRecipeFactory
import io.github.trcdevelopers.clayium.api.util.copyWithSize
import io.github.trcdevelopers.clayium.common.pan.PanRecipe
import io.github.trcdevelopers.clayium.common.recipe.ingredient.CItemRecipeInput
import io.github.trcdevelopers.clayium.common.util.DummyContainer
import net.minecraft.init.Blocks
import net.minecraft.inventory.InventoryCrafting
import net.minecraft.item.ItemStack
import net.minecraft.item.crafting.CraftingManager
import net.minecraft.item.crafting.IRecipe
import net.minecraft.util.math.BlockPos
import net.minecraft.world.IBlockAccess
import net.minecraft.world.World

object CraftingTablePanRecipeFactory : IPanRecipeFactory {

    private val ENERGY = ClayEnergy.micro(100)

    override fun getEntry(world: IBlockAccess, pos: BlockPos, stacks: List<ItemStack>, laserEnergy: Double, laserCostPerTick: ClayEnergy): IPanRecipe? {
        if (world.getBlockState(pos).block !== Blocks.CRAFTING_TABLE || world !is World) return null

        val matrix = InventoryCrafting(DummyContainer, 3, 3)
        for (slot in 0..<9) matrix.setInventorySlotContents(slot, stacks[slot])

        val recipe: IRecipe = CraftingManager.findMatchingRecipe(matrix, world) ?: return null
        val output = recipe.recipeOutput.copy()
        val inputs = recipe.ingredients.map { ingredient ->
            // field `matchingStacks` and method `getMatchingStacks` is not the same.
            // `matchingStacks` is an empty list in OreIngredient.
            val stacks = ingredient.getMatchingStacks()
            // todo: use cached recipe inputs instead of creating new one
            CItemRecipeInput(stacks.map { it.copyWithSize(1) }, 1)
        }

        return PanRecipe(inputs.filter { it.stacks.isNotEmpty() }, listOf(output), ENERGY)
    }
}