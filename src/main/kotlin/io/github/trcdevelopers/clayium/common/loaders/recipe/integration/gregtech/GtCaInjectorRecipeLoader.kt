package io.github.trcdevelopers.clayium.common.loaders.recipe.integration.gregtech

import gregtech.api.unification.material.Materials
import io.github.trcdevelopers.clayium.api.ClayEnergy
import io.github.trcdevelopers.clayium.api.unification.material.CMaterials
import io.github.trcdevelopers.clayium.api.unification.ore.OrePrefix
import io.github.trcdevelopers.clayium.common.recipe.builder.GtOrePrefix
import io.github.trcdevelopers.clayium.common.recipe.registry.CRecipes

object GtCaInjectorRecipeLoader {
    fun registerRecipes() {
        val registry = CRecipes.CA_INJECTOR

        registry.builder()
            .input(OrePrefix.gem, CMaterials.quartz)
            .input(OrePrefix.gem, CMaterials.antimatter)
            .output(GtOrePrefix.gem, Materials.Quartzite)
            .tier(10).CEt(ClayEnergy.of(1000)).duration(60)
            .build()

        // There is no niter in GTCEu

        registry.builder()
            .input(GtOrePrefix.gem, Materials.Sapphire)
            .input(OrePrefix.gem, CMaterials.antimatter)
            .output(GtOrePrefix.gem, Materials.GreenSapphire)
            .tier(10).CEt(ClayEnergy.of(1000)).duration(60)
            .buildAndRegister()

        registry.builder()
            .input(GtOrePrefix.gem, Materials.Topaz)
            .input(OrePrefix.gem, CMaterials.antimatter)
            .output(GtOrePrefix.gem, Materials.BlueTopaz)
            .tier(10).CEt(ClayEnergy.of(1000)).duration(60)
            .buildAndRegister()
    }
}