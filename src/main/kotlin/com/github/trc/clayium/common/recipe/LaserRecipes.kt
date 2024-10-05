package com.github.trc.clayium.common.recipe

import com.github.trc.clayium.common.recipe.registry.LaserRecipeRegistry
import net.minecraft.block.state.IBlockState

object LaserRecipes {
    val LASER = LaserRecipeRegistry()

    fun getLaserRecipe(input: IBlockState, energy: Double): LaserRecipe? {
        return LASER.getRecipe(input, energy)
    }
}