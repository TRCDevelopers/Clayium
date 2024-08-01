package com.github.trc.clayium.common.pan.factories

import com.github.trc.clayium.api.capability.ClayiumTileCapabilities
import com.github.trc.clayium.api.pan.IPanRecipe
import com.github.trc.clayium.api.pan.IPanRecipeFactory
import com.github.trc.clayium.api.util.getMetaTileEntity
import com.github.trc.clayium.common.pan.PanRecipe
import net.minecraft.item.ItemStack
import net.minecraft.util.math.BlockPos
import net.minecraft.world.IBlockAccess

object CPanRecipeFactory : IPanRecipeFactory {
    override fun getEntry(world: IBlockAccess, pos: BlockPos, stacks: List<ItemStack>): IPanRecipe? {
        val recipe = world.getMetaTileEntity(pos)?.getCapability(ClayiumTileCapabilities.RECIPE_LOGIC, null)
            ?.recipeRegistry?.findRecipe(Int.MAX_VALUE, stacks) ?: return null

        return PanRecipe(recipe.inputs, recipe.copyOutputs(), recipe.cePerTick * recipe.duration)
    }

}