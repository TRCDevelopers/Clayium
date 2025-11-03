package io.github.trcdevelopers.clayium.common.loaders.recipe.integration.extremereactors

import io.github.trcdevelopers.clayium.api.ClayEnergy
import io.github.trcdevelopers.clayium.api.unification.material.CMarkerMaterials
import io.github.trcdevelopers.clayium.api.unification.material.CMaterials
import io.github.trcdevelopers.clayium.api.unification.ore.OrePrefix
import io.github.trcdevelopers.clayium.common.recipe.registry.CRecipes

object ExrRecipeLoader {
    fun registerRecipes() {
        CRecipes.MATTER_TRANSFORMER.builder()
            .input(OrePrefix.ingot, CMaterials.protactinium)
            .output(OrePrefix.ingot, CMarkerMaterials.yellorium)
            .tier(9).CEt(ClayEnergy.k(5)).duration(200)
            .buildAndRegister()

        CRecipes.MATTER_TRANSFORMER.builder()
            .input(OrePrefix.ingot, CMaterials.neptunium)
            .output(OrePrefix.ingot, CMarkerMaterials.blutonium)
            .tier(10).CEt(ClayEnergy.k(20)).duration(200)
            .buildAndRegister()

        CRecipes.CA_INJECTOR.builder()
            .input(OrePrefix.ingot, CMaterials.plutonium, 8)
            .input(OrePrefix.gem, CMaterials.antimatter, 64)
            .output(OrePrefix.ingot, CMarkerMaterials.ludicrite)
            .tier(12).CEt(ClayEnergy.of(100_000)).duration(200)
            .buildAndRegister()
    }
}