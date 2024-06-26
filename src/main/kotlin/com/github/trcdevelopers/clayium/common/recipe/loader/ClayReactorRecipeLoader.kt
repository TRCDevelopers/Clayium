package com.github.trcdevelopers.clayium.common.recipe.loader

import com.github.trcdevelopers.clayium.common.clayenergy.ClayEnergy
import com.github.trcdevelopers.clayium.common.recipe.registry.CRecipes
import com.github.trcdevelopers.clayium.common.unification.OrePrefix
import com.github.trcdevelopers.clayium.common.unification.material.Material
import net.minecraft.init.Blocks

object ClayReactorRecipeLoader {
    fun register() {
        CRecipes.CLAY_REACTOR.register {
            input(Blocks.GRAVEL)
            input(OrePrefix.DUST, Material.ORGANIC_CLAY)
            output(Blocks.DIRT)
            cePerTick(ClayEnergy.of(1))
            duration(100)
        }
    }
}