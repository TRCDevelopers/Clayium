package io.github.trcdevelopers.clayium.common.loaders.recipe.extremereactors

import io.github.trcdevelopers.clayium.api.ClayEnergy
import io.github.trcdevelopers.clayium.api.unification.material.CMaterials
import io.github.trcdevelopers.clayium.api.unification.ore.OrePrefix
import io.github.trcdevelopers.clayium.common.recipe.registry.CRecipes

object ExrRecipeLoader {
    fun registerRecipes() {
        CRecipes.CA_INJECTOR.builder()
            .input(OrePrefix.ingot, CMaterials.plutonium, 8)
            .input(OrePrefix.gem, CMaterials.antimatter, 64)
            .output("ingotLudicrite")
            .tier(12).CEt(ClayEnergy.of(100_000)).duration(200)
            .buildAndRegister()
    }
}