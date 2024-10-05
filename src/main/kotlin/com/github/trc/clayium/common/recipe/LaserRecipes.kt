package com.github.trc.clayium.common.recipe

import com.github.trc.clayium.common.recipe.registry.LaserRecipeRegistry
import net.minecraft.block.Block

object LaserRecipes {
    val LASER = LaserRecipeRegistry()

    fun getLaserRecipe(input: Block, energy: Double): LaserRecipe? {
        return LASER.getRecipe(input, energy)
    }
}