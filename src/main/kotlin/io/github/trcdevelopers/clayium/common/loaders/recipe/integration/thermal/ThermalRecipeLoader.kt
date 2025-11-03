package io.github.trcdevelopers.clayium.common.loaders.recipe.integration.thermal

import io.github.trcdevelopers.clayium.api.unification.material.CMarkerMaterials
import io.github.trcdevelopers.clayium.api.unification.material.CMaterials
import io.github.trcdevelopers.clayium.api.unification.ore.OrePrefix
import io.github.trcdevelopers.clayium.common.recipe.registry.CRecipes
import net.minecraft.init.Items

object ThermalRecipeLoader {
    fun registerRecipes() {
        val ingotDust = listOf(OrePrefix.ingot, OrePrefix.dust)

        CRecipes.CLAY_REACTOR.builder()
            .input(ingotDust, CMaterials.copper, 3)
            .input(ingotDust, CMaterials.silver, 1)
            .output(OrePrefix.ingot, CMarkerMaterials.signalum, 4)
            .tier(8).duration(1_000_000_000)
            .buildAndRegister()
        CRecipes.CLAY_REACTOR.builder()
            .input(ingotDust, CMaterials.tin, 3)
            .input(ingotDust, CMaterials.silver, 1)
            .output(OrePrefix.ingot, CMarkerMaterials.lumium, 4)
            .tier(8).duration(1_000_000_000)
            .buildAndRegister()
        CRecipes.CLAY_REACTOR.builder()
            .input(OrePrefix.gem, CMarkerMaterials.diamond)
            .input(Items.REDSTONE, 2)
            .output(OrePrefix.gem, CMarkerMaterials.crystalFlux)
            .tier(7).duration(10000)
            .buildAndRegister()

        CRecipes.ALLOY_SMELTER.builder()
            .input(CMaterials.electrum)
            .input(Items.REDSTONE, 2)
            .output(OrePrefix.ingot, CMarkerMaterials.electrumFlux)
            .tier(6).duration(100)
            .buildAndRegister()
    }
}