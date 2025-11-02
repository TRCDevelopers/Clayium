package io.github.trcdevelopers.clayium.common.loaders.recipe

import io.github.trcdevelopers.clayium.api.unification.OreDictUnifier
import io.github.trcdevelopers.clayium.api.unification.material.CMarkerMaterials
import io.github.trcdevelopers.clayium.api.unification.material.CMaterials
import io.github.trcdevelopers.clayium.api.unification.ore.OrePrefix
import io.github.trcdevelopers.clayium.common.recipe.registry.CRecipes

object MiscIntegrationRecipeLoader {
    fun registerRecipes() {
        if (OreDictUnifier.exists(OrePrefix.gem, CMarkerMaterials.apatite)) {
            CRecipes.CA_INJECTOR.builder()
                .input(listOf(OrePrefix.dust, OrePrefix.gem), CMaterials.phosphorus)
                .input(OrePrefix.gem, CMaterials.antimatter)
                .output(OrePrefix.gem, CMarkerMaterials.apatite)
                .tier(10).duration(60)
                .buildAndRegister()
        }
    }
}