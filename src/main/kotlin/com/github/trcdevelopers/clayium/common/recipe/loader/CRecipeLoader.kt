package com.github.trcdevelopers.clayium.common.recipe.loader

object CRecipeLoader {
    fun load() {
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