package io.github.trcdevelopers.clayium.common.pan.factories

import io.github.trcdevelopers.clayium.api.ClayEnergy
import io.github.trcdevelopers.clayium.api.pan.IPanRecipe
import io.github.trcdevelopers.clayium.api.pan.IPanRecipeFactory
import io.github.trcdevelopers.clayium.api.util.copyWithSize
import io.github.trcdevelopers.clayium.common.pan.PanRecipe
import io.github.trcdevelopers.clayium.common.recipe.ingredient.CItemRecipeInput
import net.minecraft.init.Blocks
import net.minecraft.item.ItemStack
import net.minecraft.item.crafting.FurnaceRecipes
import net.minecraft.util.math.BlockPos
import net.minecraft.world.IBlockAccess

object FurnacePanRecipeFactory : IPanRecipeFactory {

    /** 40u x 200ticks */
    private val ENERGY = ClayEnergy.milli(8)

    override fun getEntry(world: IBlockAccess, pos: BlockPos, stacks: List<ItemStack>, laserEnergy: Double, laserCostPerTick: ClayEnergy): IPanRecipe? {
        if (world.getBlockState(pos).block !== Blocks.FURNACE) return null

        val actualStacks = stacks.filterNot(ItemStack::isEmpty)
        if (actualStacks.size != 1) return null
        val stack = actualStacks[0]
        val result = FurnaceRecipes.instance().getSmeltingResult(stack)
        if (result.isEmpty) return null
        return PanRecipe(CItemRecipeInput(stack.copyWithSize(1), 1), result, ENERGY)
    }
}