package com.github.trcdevelopers.clayium.common.loaders.recipe

import com.github.trcdevelopers.clayium.common.items.ClayiumItems
import com.github.trcdevelopers.clayium.common.recipe.registry.CRecipes

object AssemblerRecipeLoader {
    fun registerRecipes() {
        val registry = CRecipes.ASSEMBLER

        registry.builder()
            .input(ClayiumItems.CLAY_ROLLING_PIN)
            .input(ClayiumItems.CLAY_SLICER)
            .output(ClayiumItems.CLAY_IO_CONFIGURATOR)
            .tier(6).CEt(1.0).duration(20)
            .buildAndRegister()

        registry.builder()
            .input(ClayiumItems.CLAY_SPATULA)
            .input(ClayiumItems.CLAY_WRENCH)
            .output(ClayiumItems.CLAY_PIPING_TOOL)
            .tier(6).CEt(1.0).duration(20)
            .buildAndRegister()
    }
}