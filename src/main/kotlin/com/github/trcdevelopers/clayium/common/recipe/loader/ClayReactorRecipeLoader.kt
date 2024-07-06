package com.github.trcdevelopers.clayium.common.recipe.loader

import com.github.trcdevelopers.clayium.common.clayenergy.ClayEnergy
import com.github.trcdevelopers.clayium.common.recipe.registry.CRecipes
import com.github.trcdevelopers.clayium.common.unification.material.CMaterials
import com.github.trcdevelopers.clayium.common.unification.ore.OrePrefix
import net.minecraft.init.Blocks

object ClayReactorRecipeLoader {
    fun registerRecipes() {
        CRecipes.CLAY_REACTOR.register {
            input(Blocks.GRAVEL)
            input(OrePrefix.dust, CMaterials.organicClay)
            output(Blocks.DIRT)
            CEt(ClayEnergy.of(1))
            duration(100)
        }
    }
}