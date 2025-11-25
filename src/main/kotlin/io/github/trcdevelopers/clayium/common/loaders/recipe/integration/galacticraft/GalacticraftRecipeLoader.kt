package io.github.trcdevelopers.clayium.common.loaders.recipe.integration.galacticraft

import io.github.trcdevelopers.clayium.api.unification.material.CMarkerMaterials
import io.github.trcdevelopers.clayium.api.unification.material.CMaterials
import io.github.trcdevelopers.clayium.api.unification.ore.OrePrefix
import io.github.trcdevelopers.clayium.common.recipe.registry.CRecipes

object GalacticraftRecipeLoader {
    fun registerRecipes() {
        CRecipes.CA_INJECTOR.builder()
            .input(OrePrefix.ingot, CMaterials.nickel)
            .input(OrePrefix.gem, CMaterials.antimatter)
            .output(OrePrefix.ingot, CMarkerMaterials.meteoricIron)
            .tier(10).duration(60)
            .buildAndRegister()

        CRecipes.MATTER_TRANSFORMER.builder()
            .input(OrePrefix.ingot, CMarkerMaterials.meteoricIron)
            .output(OrePrefix.ingot, CMarkerMaterials.desh)
            .tier(11).duration(200)
            .buildAndRegister()
    }
}