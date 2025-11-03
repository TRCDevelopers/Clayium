package io.github.trcdevelopers.clayium.common.loaders.recipe

import io.github.trcdevelopers.clayium.api.util.CLog
import io.github.trcdevelopers.clayium.api.util.Mods
import io.github.trcdevelopers.clayium.common.config.ConfigModIntegration
import io.github.trcdevelopers.clayium.common.loaders.recipe.ae2.Ae2RecipeLoader
import io.github.trcdevelopers.clayium.common.loaders.recipe.enderio.EnderIoAlloysRecipeLoader
import io.github.trcdevelopers.clayium.common.loaders.recipe.extremereactors.ExrRecipeLoader
import io.github.trcdevelopers.clayium.common.loaders.recipe.galacticraft.GalacticraftRecipeLoader
import io.github.trcdevelopers.clayium.common.loaders.recipe.gregtech.GtAlloysRecipeLoader
import io.github.trcdevelopers.clayium.common.loaders.recipe.gregtech.GtMatterTransformerRecipeLoader
import io.github.trcdevelopers.clayium.common.loaders.recipe.mekanism.MekanismRecipeLoader
import io.github.trcdevelopers.clayium.common.loaders.recipe.metallurgy.MetallurgyRecipeLoader
import io.github.trcdevelopers.clayium.common.loaders.recipe.projectred.ProjectRedRecipeLoader
import io.github.trcdevelopers.clayium.common.loaders.recipe.sakura.SakuraRecipeLoader
import io.github.trcdevelopers.clayium.common.loaders.recipe.tconstruct.TConstructRecipeLoader
import io.github.trcdevelopers.clayium.common.loaders.recipe.thermal.ThermalRecipeLoader
import io.github.trcdevelopers.clayium.common.loaders.recipe.tofucraftreload.TofuCraftRecipeLoader
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

        MiscIntegrationRecipeLoader.registerRecipes()

        if (Mods.EnderIO.isModLoaded && ConfigModIntegration.enderio) {
            EnderIoAlloysRecipeLoader.registerRecipes()
        }
        if (Mods.ExtremeReactors.isModLoaded && ConfigModIntegration.extremeReactors) {
            ExrRecipeLoader.registerRecipes()
        }
        if (Mods.GalacticraftCore.isModLoaded && ConfigModIntegration.galacticraft) {
            GalacticraftRecipeLoader.registerRecipes()
        }
        if (Mods.GregTech.isModLoaded && ConfigModIntegration.gregtech) {
            GtAlloysRecipeLoader.registerRecipes()
            GtMatterTransformerRecipeLoader.registerRecipes()
        }
        if (Mods.Mekanism.isModLoaded && ConfigModIntegration.mekanism) {
            MekanismRecipeLoader.registerRecipes()
        }
        if (Mods.Metallurgy.isModLoaded && ConfigModIntegration.metallurgy) {
            MetallurgyRecipeLoader.registerRecipes()
        }
        if (Mods.Sakura.isModLoaded && ConfigModIntegration.sakura) {
            SakuraRecipeLoader.registerRecipes()
        }
        if (Mods.ProjectRedExpansion.isModLoaded && ConfigModIntegration.projectRedExpansion) {
            ProjectRedRecipeLoader.registerRecipes()
        }
        if (Mods.TConstruct.isModLoaded && ConfigModIntegration.tconstruct) {
            TConstructRecipeLoader.registerRecipes()
        }
        if (Mods.ThermalFoundation.isModLoaded && ConfigModIntegration.thermalFoundation) {
            ThermalRecipeLoader.registerRecipes()
        }
        if (Mods.TofuCraft.isModLoaded && ConfigModIntegration.tofuCraft) {
            TofuCraftRecipeLoader.registerRecipes()
        }
        if (Mods.AE2.isModLoaded && ConfigModIntegration.ae2) {
            Ae2RecipeLoader.registerRecipes()
        }
    }
}