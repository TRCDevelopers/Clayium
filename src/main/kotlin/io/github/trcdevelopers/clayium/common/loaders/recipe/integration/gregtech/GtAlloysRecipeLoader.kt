package io.github.trcdevelopers.clayium.common.loaders.recipe.integration.gregtech

import gregtech.api.unification.material.Materials
import io.github.trcdevelopers.clayium.api.ClayEnergy
import io.github.trcdevelopers.clayium.common.recipe.builder.GtOrePrefix
import io.github.trcdevelopers.clayium.common.recipe.registry.CRecipes

object GtAlloysRecipeLoader {
    fun registerRecipes() {
        this.registerBlastRecipes()
        this.registerAlloySmeltingRecipes()
    }

    private fun registerAlloySmeltingRecipes() {
        val registry = CRecipes.ALLOY_SMELTER

        registry.builder()
            .input(Materials.Copper)
            .input(Materials.Nickel)
            .output(GtOrePrefix.ingot, Materials.Cupronickel, 2)
            .tier(6).CEt(ClayEnergy.milli(100)).duration(100)
            .buildAndRegister()

        registry.builder()
            .input(Materials.Aluminium, 2)
            .input(Materials.Magnesium)
            .output(GtOrePrefix.ingot, Materials.Magnalium, 3)
            .tier(6).CEt(ClayEnergy.milli(100)).duration(100)
            .buildAndRegister()

        registry.builder()
            .input(Materials.Tin, 9)
            .input(Materials.Antimony)
            .output(GtOrePrefix.ingot, Materials.SolderingAlloy, 10)
            .tier(6).CEt(ClayEnergy.milli(100)).duration(100)
            .buildAndRegister()

        registry.builder()
            .input(Materials.Lead, 4)
            .input(Materials.Antimony)
            .output(GtOrePrefix.ingot, Materials.BatteryAlloy, 5)
            .tier(6).CEt(ClayEnergy.milli(100)).duration(100)
            .buildAndRegister()

        registry.builder()
            .input(Materials.Tin)
            .input(Materials.Iron)
            .output(GtOrePrefix.ingot, Materials.TinAlloy, 2)
            .tier(6).CEt(ClayEnergy.milli(100)).duration(100)
            .buildAndRegister()

        registry.builder()
            .input(Materials.Electrotine, 4)
            .input(Materials.Silver)
            .output(GtOrePrefix.ingot, Materials.BlueAlloy, 5)
            .tier(6).CEt(ClayEnergy.milli(100)).duration(100)
            .buildAndRegister()
    }

    private fun registerBlastRecipes() {
        val registry = CRecipes.CLAY_BLAST_FURNACE

        registry.builder()
            .input(Materials.Tungsten)
            .input(Materials.Steel)
            .output(GtOrePrefix.ingotHot, Materials.TungstenSteel, 2)
            .tier(10).CEt(ClayEnergy.of(1000)).duration(1000)
            .buildAndRegister()

        registry.builder()
            .input(Materials.Nickel, 4)
            .input(Materials.Chrome, 4)
            .output(GtOrePrefix.ingotHot, Materials.Nichrome, 5)
            .tier(9).CEt(ClayEnergy.of(100)).duration(1000)
            .buildAndRegister()

        registry.builder()
            .input(Materials.Vanadium, 3)
            .input(Materials.Gallium, 1)
            .output(GtOrePrefix.ingotHot, Materials.VanadiumGallium, 4)
            .tier(9).CEt(ClayEnergy.of(100)).duration(1000)
            .buildAndRegister()

        registry.builder()
            .input(Materials.Niobium)
            .input(Materials.Titanium)
            .output(GtOrePrefix.ingotHot, Materials.NiobiumTitanium, 2)
            .tier(9).CEt(ClayEnergy.of(100)).duration(1000)
            .buildAndRegister()
    }
}