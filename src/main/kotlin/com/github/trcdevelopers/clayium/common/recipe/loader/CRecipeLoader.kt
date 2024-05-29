package com.github.trcdevelopers.clayium.common.recipe.loader

object CRecipeLoader {
    fun load() {
        BendingMachineLoader.register()
        ClayBlastFurnaceRecipeLoader.register()
        ClayWorkTableRecipes.register()
        ClayReactorRecipeLoader.register()
        CondenserRecipeLoader.registerRecipes()
    }
}