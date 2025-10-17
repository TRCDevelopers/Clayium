package io.github.trcdevelopers.clayium.common.loaders.recipe.gregtech

import gregtech.api.unification.material.Materials
import io.github.trcdevelopers.clayium.api.ClayEnergy
import io.github.trcdevelopers.clayium.api.unification.ore.OrePrefix
import io.github.trcdevelopers.clayium.common.recipe.builder.GtOrePrefix
import io.github.trcdevelopers.clayium.common.recipe.registry.CRecipes

object GtBlastFurnaceRecipeLoader {
    fun registerRecipes() {
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