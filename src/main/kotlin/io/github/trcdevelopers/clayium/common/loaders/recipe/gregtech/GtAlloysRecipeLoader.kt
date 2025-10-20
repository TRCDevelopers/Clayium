package io.github.trcdevelopers.clayium.common.loaders.recipe.gregtech

import gregtech.api.unification.material.Materials
import io.github.trcdevelopers.clayium.api.ClayEnergy
import io.github.trcdevelopers.clayium.api.unification.ore.OrePrefix
import io.github.trcdevelopers.clayium.common.recipe.builder.GtOrePrefix
import io.github.trcdevelopers.clayium.common.recipe.registry.CRecipes

object GtAlloysRecipeLoader {
    fun registerRecipes() {
        this.registerBlastRecipes()
        this.registerAlloySmeltingRecipes()
    }

    private fun registerAlloySmeltingRecipes() {
        val registry = CRecipes.ALLOY_SMELTER
        val prefixes = listOf(OrePrefix.ingot, OrePrefix.dust)

        registry.builder()
            .input(prefixes, Materials.Copper)
            .input(prefixes, Materials.Nickel)
            .output(GtOrePrefix.ingot, Materials.Cupronickel, 2)
            .tier(6).CEt(ClayEnergy.milli(100)).duration(100)
            .buildAndRegister()

        registry.builder()
            .input(prefixes, Materials.Aluminium, 2)
            .input(prefixes, Materials.Magnesium)
            .output(GtOrePrefix.ingot, Materials.Magnalium, 3)
            .tier(6).CEt(ClayEnergy.milli(100)).duration(100)
            .buildAndRegister()

        registry.builder()
            .input(prefixes, Materials.Tin, 9)
            .input(prefixes, Materials.Antimony)
            .output(GtOrePrefix.ingot, Materials.SolderingAlloy, 10)
            .tier(6).CEt(ClayEnergy.milli(100)).duration(100)
            .buildAndRegister()

        registry.builder()
            .input(prefixes, Materials.Lead, 4)
            .input(prefixes, Materials.Antimony)
            .output(GtOrePrefix.ingot, Materials.BatteryAlloy, 5)
            .tier(6).CEt(ClayEnergy.milli(100)).duration(100)
            .buildAndRegister()

        registry.builder()
            .input(prefixes, Materials.Tin)
            .input(prefixes, Materials.Iron)
            .output(GtOrePrefix.ingot, Materials.TinAlloy, 2)
            .tier(6).CEt(ClayEnergy.milli(100)).duration(100)
            .buildAndRegister()

        registry.builder()
            .input(prefixes, Materials.Electrotine, 4)
            .input(prefixes, Materials.Silver)
            .output(GtOrePrefix.ingot, Materials.BlueAlloy, 5)
            .tier(6).CEt(ClayEnergy.milli(100)).duration(100)
            .buildAndRegister()
    }

    private fun registerBlastRecipes() {
        val registry = CRecipes.CLAY_BLAST_FURNACE

        val prefixes = listOf(OrePrefix.ingot, OrePrefix.dust)

        registry.builder()
            .input(prefixes, Materials.Tungsten)
            .input(prefixes, Materials.Steel)
            .output(GtOrePrefix.ingotHot, Materials.TungstenSteel, 2)
            .tier(10).CEt(ClayEnergy.of(1000)).duration(1000)
            .buildAndRegister()

        registry.builder()
            .input(prefixes, Materials.Nickel, 4)
            .input(prefixes, Materials.Chrome, 4)
            .output(GtOrePrefix.ingotHot, Materials.Nichrome, 5)
            .tier(9).CEt(ClayEnergy.of(100)).duration(1000)
            .buildAndRegister()

        registry.builder()
            .input(prefixes, Materials.Vanadium, 3)
            .input(prefixes, Materials.Gallium, 1)
            .output(GtOrePrefix.ingotHot, Materials.VanadiumGallium, 4)
            .tier(9).CEt(ClayEnergy.of(100)).duration(1000)
            .buildAndRegister()

        registry.builder()
            .input(prefixes, Materials.Niobium)
            .input(prefixes, Materials.Titanium)
            .output(GtOrePrefix.ingotHot, Materials.NiobiumTitanium, 2)
            .tier(9).CEt(ClayEnergy.of(100)).duration(1000)
            .buildAndRegister()
    }
}