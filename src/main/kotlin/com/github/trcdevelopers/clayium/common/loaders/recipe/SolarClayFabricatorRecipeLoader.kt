package com.github.trcdevelopers.clayium.common.loaders.recipe

import com.github.trcdevelopers.clayium.common.recipe.registry.CRecipes
import com.github.trcdevelopers.clayium.common.unification.material.CMaterials
import com.github.trcdevelopers.clayium.common.unification.ore.OrePrefix

object SolarClayFabricatorRecipeLoader {
    fun registerRecipes() {
        val block = OrePrefix.block

        val clays = mutableListOf(CMaterials.clay, CMaterials.denseClay, CMaterials.compressedClay,
            CMaterials.industrialClay, CMaterials.advancedIndustrialClay)

        for (clay in clays) CRecipes.SOLAR_1.register { input(block, clay).tier(5) }

        clays.add(CMaterials.energeticClay)
        clays.add(CMaterials.compressedEnergeticClay)

        for (clay in clays) CRecipes.SOLAR_2.register { input(block, clay).tier(6) }

        clays.add(CMaterials.compressedEnergeticClay2)
        clays.add(CMaterials.compressedEnergeticClay3)
        clays.add(CMaterials.compressedEnergeticClay4)

        for (clay in clays) CRecipes.SOLAR_3.register { input(block, clay).tier(7) }
    }
}