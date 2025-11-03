package io.github.trcdevelopers.clayium.common.loaders.recipe.integration.ae2

import io.github.trcdevelopers.clayium.api.unification.material.CMarkerMaterials
import io.github.trcdevelopers.clayium.api.unification.ore.OrePrefix
import io.github.trcdevelopers.clayium.common.recipe.registry.CRecipes
import net.minecraft.init.Items

object Ae2RecipeLoader {
    fun registerRecipes() {
        CRecipes.MATTER_TRANSFORMER.builder()
            .input(Items.QUARTZ)
            .output(OrePrefix.crystal, CMarkerMaterials.certusQuartz)
            .tier(10).defaultCEt().duration(60)
            .buildAndRegister()
    }
}