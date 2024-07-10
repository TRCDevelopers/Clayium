package com.github.trcdevelopers.clayium.common.recipe.loader

import com.github.trcdevelopers.clayium.common.recipe.handler.MaterialRecipeHandler

object CRecipeLoader {
    fun load() {
        MaterialRecipeHandler.registerRecipes()

        AssemblerRecipeLoader.registerRecipes()
        BendingMachineLoader.registerRecipes()
        CaCondenserRecipeLoader.registerRecipes()
        ChemicalReactorRecipeLoader.registerRecipes()
        ClayBlastFurnaceRecipeLoader.registerRecipes()
        ClayReactorRecipeLoader.registerRecipes()
        ClayWorkTableRecipes.registerRecipes()
        CondenserRecipeLoader.registerRecipes()
        CuttingMachineRecipeLoader.registerRecipes()
        InscriberRecipeLoader.registerRecipes()
        LatheRecipeLoader.registerRecipes()
        MachineBlockRecipeLoader.registerRecipes()
        MatterTransformerRecipeLoader.registerRecipes()
        MillingMachineRecipeLoader.registerRecipes()
        PipeDrawingRecipeLoader.registerRecipes()
        SolarClayFabricatorRecipeLoader.registerRecipes()
        WireDrawingRecipeLoader.registerRecipes()
    }
}