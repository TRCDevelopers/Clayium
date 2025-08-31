package io.github.trcdevelopers.clayium.common.loaders.recipe

import io.github.trcdevelopers.clayium.api.ClayEnergy
import io.github.trcdevelopers.clayium.api.unification.material.CMaterials
import io.github.trcdevelopers.clayium.api.unification.ore.OrePrefix
import io.github.trcdevelopers.clayium.common.recipe.registry.CRecipes

object PipeDrawingRecipeLoader {
    fun registerRecipes() {
        for (material in listOf(CMaterials.clay, CMaterials.denseClay)) {
            CRecipes.PIPE_DRAWING_MACHINE.builder()
                .input(OrePrefix.cylinder, material)
                .output(OrePrefix.pipe, material, 2)
                .tier(0).CEt(ClayEnergy.micro(10)).duration(3)
                .buildAndRegister()
        }
    }
}