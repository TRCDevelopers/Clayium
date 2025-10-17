package io.github.trcdevelopers.clayium.common.loaders.recipe

import io.github.trcdevelopers.clayium.api.util.Mods
import io.github.trcdevelopers.clayium.common.loaders.recipe.gregtech.GtBlastFurnaceRecipeLoader
import io.github.trcdevelopers.clayium.common.loaders.recipe.gregtech.GtMatterTransformerRecipeLoader
import io.github.trcdevelopers.clayium.common.recipe.handler.MaterialRecipeHandler

object CRecipeLoader {
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
        EnergeticClayDecomposerRecipeLoader.registerRecipes()
        GrinderRecipeLoader.registerRecipes()
        InscriberRecipeLoader.registerRecipes()
        LatheRecipeLoader.registerRecipes()
        MachineBlockRecipeLoader.registerRecipes()
        MatterTransformerRecipeLoader.registerRecipes()
        MillingMachineRecipeLoader.registerRecipes()
        PipeDrawingRecipeLoader.registerRecipes()
        SolarClayFabricatorRecipeLoader.registerRecipes()
        WireDrawingRecipeLoader.registerRecipes()

        ClayGadgetsRecipeLoader.registerRecipes()

        if (Mods.GregTech.isModLoaded) {
            GtBlastFurnaceRecipeLoader.registerRecipes()
            GtMatterTransformerRecipeLoader.registerRecipes()
        }
    }
}