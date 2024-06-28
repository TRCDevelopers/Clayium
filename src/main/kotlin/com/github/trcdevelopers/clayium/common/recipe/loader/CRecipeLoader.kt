package com.github.trcdevelopers.clayium.common.recipe.loader

import com.github.trcdevelopers.clayium.common.recipe.handler.MaterialRecipeHandler

object CRecipeLoader {
    fun load() {
        MaterialRecipeHandler.registerRecipes()

        BendingMachineLoader.register()
        CaCondenserRecipeLoader.registerRecipes()
        ClayBlastFurnaceRecipeLoader.register()
        ClayWorkTableRecipes.register()
        ClayReactorRecipeLoader.register()
        CondenserRecipeLoader.registerRecipes()
        SolarClayFabricatorRecipeLoader.register()
        MatterTransformerRecipeLoader.registerRecipes()
    }
}