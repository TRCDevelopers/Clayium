package com.github.trc.clayium.common.loaders.recipe

import com.github.trc.clayium.api.ClayEnergy
import com.github.trc.clayium.api.unification.material.CMaterials
import com.github.trc.clayium.api.unification.ore.OrePrefix
import com.github.trc.clayium.common.recipe.registry.CRecipes

object PipeDrawingRecipeLoader {
    fun registerRecipes() {
        for (material in listOf(CMaterials.clay, CMaterials.denseClay)) {
            CRecipes.PIPE_DRAWING_MACHINE.builder()
                .input(OrePrefix.cylinder, material)
                .output(OrePrefix.pipe, material, 2)
                .tier(0)
                .CEt(ClayEnergy.micro(10))
                .duration(3)
                .buildAndRegister()
        }
    }
}
