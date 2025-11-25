package io.github.trcdevelopers.clayium.common.loaders.recipe.integration.gregtech

import gregtech.api.unification.material.Materials
import io.github.trcdevelopers.clayium.api.unification.material.CMaterials
import io.github.trcdevelopers.clayium.api.unification.ore.OrePrefix
import io.github.trcdevelopers.clayium.common.recipe.builder.GtOrePrefix
import io.github.trcdevelopers.clayium.common.recipe.registry.CRecipes

object GtMatterTransformerRecipeLoader {
    fun registerRecipes() {
        val registry = CRecipes.MATTER_TRANSFORMER

        registry.builder()
            .input(OrePrefix.ingot, CMaterials.aluminum)
            .output(GtOrePrefix.ingot, Materials.Gallium)
            .tier(11).defaultCEt().duration(200)
            .buildAndRegister()

        registry.builder()
            .defaultPrefix(OrePrefix.ingot)
            .tier(11).defaultCEt().duration(200)
            .input(Materials.Vanadium)
            .output(Materials.Niobium)
            .chain(Materials.Yttrium)
            .buildAndRegister()

        registry.builder()
            .defaultPrefix(OrePrefix.ingot)
            .tier(12).defaultCEt().duration(200)
            .input(Materials.Europium)
            .output(Materials.Naquadah)
            .chain(Materials.NaquadahEnriched)
            .chain(Materials.Naquadria)
            .buildAndRegister()

        registry.builder()
            .defaultPrefix(OrePrefix.ingot)
            .tier(12).defaultCEt().duration(200)
            .input(Materials.Curium).output(Materials.Neutronium)
            .buildAndRegister()

        registry.builder()
            .defaultPrefix(OrePrefix.dust)
            .tier(9).defaultCEt().duration(200)
            // Original version has Nikolite in between, but it's gone. So 2x duration and skip it.
            .input(Materials.Redstone).output(Materials.Electrotine)
            .buildAndRegister()
    }
}