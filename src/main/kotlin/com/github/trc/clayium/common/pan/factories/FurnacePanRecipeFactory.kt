package com.github.trc.clayium.common.pan.factories

import com.github.trc.clayium.api.pan.IPanRecipe
import com.github.trc.clayium.api.pan.IPanRecipeFactory
import com.github.trc.clayium.api.util.copyWithSize
import com.github.trc.clayium.common.clayenergy.ClayEnergy
import com.github.trc.clayium.common.pan.PanRecipe
import com.github.trc.clayium.common.recipe.ingredient.CItemRecipeInput
import net.minecraft.init.Blocks
import net.minecraft.item.ItemStack
import net.minecraft.item.crafting.FurnaceRecipes
import net.minecraft.util.math.BlockPos
import net.minecraft.world.IBlockAccess

object FurnacePanRecipeFactory : IPanRecipeFactory {
    override fun getEntry(world: IBlockAccess, pos: BlockPos, stacks: List<ItemStack>): IPanRecipe? {
        if (world.getBlockState(pos).block !== Blocks.FURNACE) return null

        val actualStacks = stacks.filterNot(ItemStack::isEmpty)
        if (actualStacks.size != 1) return null
        val stack = actualStacks[0]
        val result = FurnaceRecipes.instance().getSmeltingResult(stack)
        if (result.isEmpty) return null
        return PanRecipe(CItemRecipeInput(stack.copyWithSize(1), 1), result, ClayEnergy(1))
    }
}