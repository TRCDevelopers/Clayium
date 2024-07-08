package com.github.trcdevelopers.clayium.common.recipe.loader

import com.github.trcdevelopers.clayium.common.clayenergy.ClayEnergy
import com.github.trcdevelopers.clayium.common.items.metaitem.MetaItemClayParts
import com.github.trcdevelopers.clayium.common.recipe.registry.CRecipes

object PipeDrawingRecipeLoader {
    fun registerRecipes() {
        val registry = CRecipes.PIPE_DRAWING_MACHINE

        registry.builder()
            .input(MetaItemClayParts.CLAY_CYLINDER)
            .output(MetaItemClayParts.CLAY_PIPE, 2)
            .tier(0).CEt(ClayEnergy.micro(10)).duration(3)
            .buildAndRegister()

        registry.builder()
            .input(MetaItemClayParts.DENSE_CLAY_CYLINDER)
            .output(MetaItemClayParts.DENSE_CLAY_PIPE, 2)
            .tier(0).CEt(ClayEnergy.micro(10)).duration(3)
            .buildAndRegister()
    }
}