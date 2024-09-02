package com.github.trc.clayium.common.loaders.recipe

import com.github.trc.clayium.api.unification.material.CMaterials
import com.github.trc.clayium.api.unification.ore.OrePrefix
import com.github.trc.clayium.common.recipe.registry.CRecipes

object SolarClayFabricatorRecipeLoader {
    fun registerRecipes() {
        val block = OrePrefix.block

        val clays = mutableListOf(CMaterials.clay, CMaterials.denseClay, CMaterials.compressedClay,
            CMaterials.industrialClay, CMaterials.advancedIndustrialClay)

        for (clay in clays) CRecipes.SOLAR_1.register { clayInput(block, clay).tier(5) }

        clays.add(CMaterials.energeticClay)
        clays.add(CMaterials.compressedEnergeticClay)

        for (clay in clays) CRecipes.SOLAR_2.register { clayInput(block, clay).tier(6) }

        clays.add(CMaterials.compressedEnergeticClay2)
        clays.add(CMaterials.compressedEnergeticClay3)
        clays.add(CMaterials.compressedEnergeticClay4)

        for (clay in clays) CRecipes.SOLAR_3.register { clayInput(block, clay).tier(7) }
    }
}