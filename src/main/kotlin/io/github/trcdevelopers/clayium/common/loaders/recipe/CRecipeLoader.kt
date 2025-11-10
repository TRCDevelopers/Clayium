package io.github.trcdevelopers.clayium.common.loaders.recipe

import io.github.trcdevelopers.clayium.api.util.Mods
import io.github.trcdevelopers.clayium.common.config.ConfigRecipe
import io.github.trcdevelopers.clayium.common.loaders.recipe.integration.MiscIntegrationRecipeLoader
import io.github.trcdevelopers.clayium.common.loaders.recipe.integration.ae2.Ae2RecipeLoader
import io.github.trcdevelopers.clayium.common.loaders.recipe.integration.enderio.EnderIoAlloysRecipeLoader
import io.github.trcdevelopers.clayium.common.loaders.recipe.integration.extremereactors.ExrRecipeLoader
import io.github.trcdevelopers.clayium.common.loaders.recipe.integration.galacticraft.GalacticraftRecipeLoader
import io.github.trcdevelopers.clayium.common.loaders.recipe.integration.gregtech.GtAlloysRecipeLoader
import io.github.trcdevelopers.clayium.common.loaders.recipe.integration.gregtech.GtCaInjectorRecipeLoader
import io.github.trcdevelopers.clayium.common.loaders.recipe.integration.gregtech.GtMatterTransformerRecipeLoader
import io.github.trcdevelopers.clayium.common.loaders.recipe.integration.mekanism.MekanismRecipeLoader
import io.github.trcdevelopers.clayium.common.loaders.recipe.integration.metallurgy.MetallurgyRecipeLoader
import io.github.trcdevelopers.clayium.common.loaders.recipe.integration.projectred.ProjectRedRecipeLoader
import io.github.trcdevelopers.clayium.common.loaders.recipe.integration.sakura.SakuraRecipeLoader
import io.github.trcdevelopers.clayium.common.loaders.recipe.integration.tconstruct.TConstructRecipeLoader
import io.github.trcdevelopers.clayium.common.loaders.recipe.integration.thermal.ThermalRecipeLoader
import io.github.trcdevelopers.clayium.common.loaders.recipe.integration.tofucraftreload.TofuCraftRecipeLoader
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

        MiscIntegrationRecipeLoader.registerMandatory()
        if (ConfigRecipe.integration.enableIntegrationRecipes) {
            MiscIntegrationRecipeLoader.registerRecipes()

            if (Mods.EnderIO.isModLoaded && ConfigRecipe.integration.enderio) {
                EnderIoAlloysRecipeLoader.registerRecipes()
            }
            if (Mods.ExtremeReactors.isModLoaded && ConfigRecipe.integration.extremeReactors) {
                ExrRecipeLoader.registerRecipes()
            }
            if (Mods.GalacticraftCore.isModLoaded && ConfigRecipe.integration.galacticraft) {
                GalacticraftRecipeLoader.registerRecipes()
            }
            if (Mods.GregTech.isModLoaded && ConfigRecipe.integration.gregtech) {
                GtAlloysRecipeLoader.registerRecipes()
                GtMatterTransformerRecipeLoader.registerRecipes()
                GtCaInjectorRecipeLoader.registerRecipes()
            }
            if (Mods.Mekanism.isModLoaded && ConfigRecipe.integration.mekanism) {
                MekanismRecipeLoader.registerRecipes()
            }
            if (Mods.Metallurgy.isModLoaded && ConfigRecipe.integration.metallurgy) {
                MetallurgyRecipeLoader.registerRecipes()
            }
            if (Mods.Sakura.isModLoaded && ConfigRecipe.integration.sakura) {
                SakuraRecipeLoader.registerRecipes()
            }
            if (Mods.ProjectRedExpansion.isModLoaded && ConfigRecipe.integration.projectRedExpansion) {
                ProjectRedRecipeLoader.registerRecipes()
            }
            if (Mods.TConstruct.isModLoaded && ConfigRecipe.integration.tconstruct) {
                TConstructRecipeLoader.registerRecipes()
            }
            if (Mods.ThermalFoundation.isModLoaded && ConfigRecipe.integration.thermalFoundation) {
                ThermalRecipeLoader.registerRecipes()
            }
            if (Mods.TofuCraft.isModLoaded && ConfigRecipe.integration.tofuCraft) {
                TofuCraftRecipeLoader.registerRecipes()
            }
            if (Mods.AE2.isModLoaded && ConfigRecipe.integration.ae2) {
                Ae2RecipeLoader.registerRecipes()
            }
        }
    }
}