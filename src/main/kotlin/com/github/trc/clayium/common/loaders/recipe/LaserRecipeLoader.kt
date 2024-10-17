package com.github.trc.clayium.common.loaders.recipe

import com.github.trc.clayium.common.blocks.ClayiumBlocks
import com.github.trc.clayium.common.recipe.registry.CRecipes
import net.minecraft.init.Blocks

object LaserRecipeLoader {
    fun registerRecipes() {
        CRecipes.LASER.register {
            input(Blocks.SAPLING)
            output(ClayiumBlocks.CLAY_TREE_SAPLING.defaultState)
            energyMin(1000.0)
            requiredEnergy(300000.0)
        }
        CRecipes.LASER.register {
            input(Blocks.SAPLING, 0)
            output(Blocks.SAPLING, 1)
            energyMax(1000.0)
            requiredEnergy(300.0)
        }
        CRecipes.LASER.register {
            input(Blocks.SAPLING, 1)
            output(Blocks.SAPLING, 2)
            energyMax(1000.0)
            requiredEnergy(300.0)
        }
        CRecipes.LASER.register {
            input(Blocks.SAPLING, 2)
            output(Blocks.SAPLING, 3)
            energyMax(1000.0)
            requiredEnergy(300.0)
        }
        CRecipes.LASER.register {
            input(Blocks.SAPLING, 3)
            output(Blocks.SAPLING, 4)
            energyMax(1000.0)
            requiredEnergy(300.0)
        }
        CRecipes.LASER.register {
            input(Blocks.SAPLING, 4)
            output(Blocks.SAPLING, 5)
            energyMax(1000.0)
            requiredEnergy(300.0)
        }
        CRecipes.LASER.register {
            input(Blocks.SAPLING, 5)
            output(Blocks.SAPLING, 0)
            energyMax(1000.0)
            requiredEnergy(300.0)
        }
    }
}
