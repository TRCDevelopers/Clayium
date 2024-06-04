package com.github.trcdevelopers.clayium.common.recipe.loader

import com.github.trcdevelopers.clayium.common.blocks.ClayiumBlocks
import com.github.trcdevelopers.clayium.common.recipe.registry.CRecipes
import net.minecraft.init.Blocks
import net.minecraft.item.ItemStack

object SolarClayFabricatorRecipeLoader {
    fun register() {
        CRecipes.SOLAR_CLAY_FABRICATOR.register {
            input(Blocks.CLAY)
            tier(5)
        }

        for (i in 0..3) {
            CRecipes.SOLAR_CLAY_FABRICATOR.register {
                input(ItemStack(ClayiumBlocks.COMPRESSED_CLAY, 1, i))
                tier(5)
            }
        }
    }
}