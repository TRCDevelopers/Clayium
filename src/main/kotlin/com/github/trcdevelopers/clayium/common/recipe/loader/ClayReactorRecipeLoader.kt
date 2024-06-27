package com.github.trcdevelopers.clayium.common.recipe.loader

import com.github.trcdevelopers.clayium.common.clayenergy.ClayEnergy
import com.github.trcdevelopers.clayium.common.recipe.registry.CRecipes
import com.github.trcdevelopers.clayium.common.unification.EnumOrePrefix
import com.github.trcdevelopers.clayium.common.unification.material.CMaterials
import com.github.trcdevelopers.clayium.common.unification.material.EnumMaterial
import com.github.trcdevelopers.clayium.common.unification.ore.OrePrefix
import net.minecraft.init.Blocks

object ClayReactorRecipeLoader {
    fun register() {
        CRecipes.CLAY_REACTOR.register {
            input(Blocks.GRAVEL)
            input(OrePrefix.dust, CMaterials.organicClay)
            output(Blocks.DIRT)
            cePerTick(ClayEnergy.of(1))
            duration(100)
        }
    }
}