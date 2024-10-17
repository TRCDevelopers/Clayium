package com.github.trc.clayium.common.loaders.recipe

import com.github.trc.clayium.common.recipe.handler.MaterialRecipeHandler

object CRecipeLoader {
    // todo refactoring
    fun load() {
        ClayWorkTableRecipeLoader.registerRecipes()

        LaserRecipeLoader.registerRecipes()

        CraftingRecipeLoader.registerRecipes()
        MaterialRecipeHandler.registerRecipes()

        AlloySmelterRecipeLoader.registerRecipes()
        AssemblerRecipeLoader.registerRecipes()
        BendingMachineLoader.registerRecipes()
        CaCondenserRecipeLoader.registerRecipes()
        CaInjectorRecipeLoader.registerRecipes()
        CentrifugeRecipeLoader.registerRecipes()
        ChemicalMetalSeparatorRecipeLoader.registerRecipes()
        ChemicalReactorRecipeLoader.registerRecipes()
        ClayBlastFurnaceRecipeLoader.registerRecipes()
        ClayReactorRecipeLoader.registerRecipes()
        CondenserRecipeLoader.registerRecipes()
        CuttingMachineRecipeLoader.registerRecipes()
        DecomposerRecipeLoader.registerRecipes()
        GrinderRecipeLoader.registerRecipes()
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
