package com.github.trc.clayium.common.recipe

import com.github.trc.clayium.common.recipe.registry.CRecipes.LASER
import net.minecraft.block.state.IBlockState

object LaserRecipes {

    fun getLaserRecipe(input: IBlockState, energy: Double): LaserRecipe? {
        return LASER.getRecipe(input, energy)
    }
}