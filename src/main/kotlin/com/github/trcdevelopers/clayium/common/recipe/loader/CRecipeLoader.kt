package com.github.trcdevelopers.clayium.common.recipe.loader

import com.github.trcdevelopers.clayium.common.recipe.handler.MaterialRecipeHandler

object CRecipeLoader {
    fun load() {
        MaterialRecipeHandler.registerRecipes()

        BendingMachineLoader.registerRecipes()
        CaCondenserRecipeLoader.registerRecipes()
        ChemicalReactorRecipeLoader.registerRecipes()
        ClayBlastFurnaceRecipeLoader.registerRecipes()
        ClayWorkTableRecipes.registerRecipes()
        ClayReactorRecipeLoader.registerRecipes()
        CondenserRecipeLoader.registerRecipes()
        SolarClayFabricatorRecipeLoader.registerRecipes()
        MatterTransformerRecipeLoader.registerRecipes()
        MillingMachineRecipeLoader.registerRecipes()
    }
}