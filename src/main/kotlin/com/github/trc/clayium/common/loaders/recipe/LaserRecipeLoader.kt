package com.github.trc.clayium.common.loaders.recipe

import com.github.trc.clayium.common.blocks.ClayiumBlocks
import com.github.trc.clayium.common.recipe.LaserRecipes
import net.minecraft.init.Blocks

object LaserRecipeLoader {
    fun registerRecipes() {
        LaserRecipes.LASER.register {
            input(Blocks.SAPLING)
            output(ClayiumBlocks.CLAY_TREE_SAPLING)
            energyMin(1000.0)
            requiredEnergy(300000.0)
        }
    }
}