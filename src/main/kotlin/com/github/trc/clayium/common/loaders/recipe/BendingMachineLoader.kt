package com.github.trc.clayium.common.loaders.recipe

import com.github.trc.clayium.api.ClayEnergy
import com.github.trc.clayium.api.unification.material.CMaterials
import com.github.trc.clayium.api.unification.ore.OrePrefix
import com.github.trc.clayium.common.recipe.registry.CRecipes

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