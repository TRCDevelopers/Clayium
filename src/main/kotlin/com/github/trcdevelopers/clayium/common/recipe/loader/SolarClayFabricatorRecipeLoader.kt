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
    }
}