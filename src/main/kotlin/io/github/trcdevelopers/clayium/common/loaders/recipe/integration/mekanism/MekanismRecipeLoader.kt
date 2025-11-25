package io.github.trcdevelopers.clayium.common.loaders.recipe.integration.mekanism

import io.github.trcdevelopers.clayium.api.unification.material.CMarkerMaterials
import io.github.trcdevelopers.clayium.api.unification.material.CMaterials
import io.github.trcdevelopers.clayium.api.unification.ore.OrePrefix
import io.github.trcdevelopers.clayium.common.recipe.registry.CRecipes
import net.minecraft.init.Items

object MekanismRecipeLoader {
    fun registerRecipes() {
        CRecipes.ALLOY_SMELTER.builder()
            .input(CMaterials.osmium)
            .input(Items.GLOWSTONE_DUST)
            .output(OrePrefix.ingot, CMarkerMaterials.refinedGlowstone)
            .tier(6).duration(100)
            .buildAndRegister()

        CRecipes.CLAY_REACTOR.builder()
            .input(listOf(OrePrefix.gem, OrePrefix.dust), CMarkerMaterials.diamond)
            .input(listOf(OrePrefix.block, OrePrefix.dust), CMarkerMaterials.obsidian)
            .output(OrePrefix.ingot, CMarkerMaterials.refinedObsidian)
            .tier(8).duration(1_000_000_000)
            .buildAndRegister()
    }
}