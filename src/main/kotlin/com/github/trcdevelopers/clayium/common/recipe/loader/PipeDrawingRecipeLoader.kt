package com.github.trcdevelopers.clayium.common.recipe.loader

import com.github.trcdevelopers.clayium.common.clayenergy.ClayEnergy
import com.github.trcdevelopers.clayium.common.items.metaitem.MetaItemClayParts
import com.github.trcdevelopers.clayium.common.recipe.registry.CRecipes
import com.github.trcdevelopers.clayium.common.unification.material.CMaterials
import com.github.trcdevelopers.clayium.common.unification.ore.OrePrefix

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