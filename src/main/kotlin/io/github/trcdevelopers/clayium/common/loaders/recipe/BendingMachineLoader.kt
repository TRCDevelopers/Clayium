package io.github.trcdevelopers.clayium.common.loaders.recipe

import io.github.trcdevelopers.clayium.api.ClayEnergy
import io.github.trcdevelopers.clayium.api.unification.material.CMaterials
import io.github.trcdevelopers.clayium.api.unification.ore.OrePrefix
import io.github.trcdevelopers.clayium.common.recipe.registry.CRecipes

object BendingMachineLoader {
    fun registerRecipes() {
        val registry = CRecipes.BENDING
        for ((i, m) in listOf(CMaterials.clay, CMaterials.denseClay).withIndex()) {
            registry.builder()
                .input(OrePrefix.cylinder, m)
                .output(OrePrefix.blade, m, 2)
                .tier(0).CEt(ClayEnergy.micro(10))
                .duration(4 * (i + 1))
                .buildAndRegister()
        }
    }
}