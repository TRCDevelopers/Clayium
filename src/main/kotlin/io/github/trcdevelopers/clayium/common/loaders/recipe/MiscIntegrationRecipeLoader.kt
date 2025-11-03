package io.github.trcdevelopers.clayium.common.loaders.recipe

import io.github.trcdevelopers.clayium.api.unification.OreDictUnifier
import io.github.trcdevelopers.clayium.api.unification.material.CMarkerMaterials
import io.github.trcdevelopers.clayium.api.unification.material.CMaterials
import io.github.trcdevelopers.clayium.api.unification.ore.OrePrefix
import io.github.trcdevelopers.clayium.common.recipe.registry.CRecipes
import net.minecraft.init.Items

object MiscIntegrationRecipeLoader {
    fun registerRecipes() {
        val caInjector = CRecipes.CA_INJECTOR
        val reactor = CRecipes.CLAY_REACTOR
        val matterTransformer = CRecipes.MATTER_TRANSFORMER
        if (OreDictUnifier.exists(OrePrefix.gem, CMarkerMaterials.apatite)) {
            caInjector.builder()
                .input(listOf(OrePrefix.dust, OrePrefix.gem), CMaterials.phosphorus)
                .inputAntimatter(1)
                .output(OrePrefix.gem, CMarkerMaterials.apatite)
                .tier(10).duration(60)
                .buildAndRegister()
        }

        if (OreDictUnifier.exists(OrePrefix.gem, CMarkerMaterials.topaz)) {
            caInjector.builder()
                .input(OrePrefix.gem, CMarkerMaterials.emerald)
                .inputAntimatter(1)
                .output(OrePrefix.gem, CMarkerMaterials.topaz)
                .tier(10).duration(60)
                .buildAndRegister()

            if (OreDictUnifier.exists(OrePrefix.gem, CMarkerMaterials.malachite)
                && OreDictUnifier.exists(OrePrefix.gem, CMarkerMaterials.tanzanite)) {
                matterTransformer.builder()
                    .tier(10).duration(200)
                    .input(OrePrefix.gem, CMarkerMaterials.topaz)
                    .output(OrePrefix.gem, CMarkerMaterials.malachite)
                    .chain(OrePrefix.gem, CMarkerMaterials.tanzanite)
                    .buildAndRegister()
            }
        }

        if (OreDictUnifier.exists(OrePrefix.gem, CMarkerMaterials.dilithium)) {
            reactor.builder()
                .input(OrePrefix.gem, CMaterials.quartz)
                .input(OrePrefix.dust, CMaterials.lithium)
                .output(OrePrefix.gem, CMarkerMaterials.dilithium)
                .tier(8).duration(1_000_000_000)
                .buildAndRegister()
        }

        if (OreDictUnifier.exists(OrePrefix.gem, CMarkerMaterials.forcicium)) {
            reactor.builder()
                .input(OrePrefix.gem, CMaterials.quartz)
                .input(Items.REDSTONE, 4)
                .output(OrePrefix.gem, CMarkerMaterials.forcicium)
                .tier(8).duration(1_000_000_000)
        }
    }
}