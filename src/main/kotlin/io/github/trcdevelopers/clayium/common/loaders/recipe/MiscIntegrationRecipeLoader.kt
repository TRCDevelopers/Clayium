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

        matterTransformer.builder()
            .defaultPrefix(OrePrefix.dust).duration(200)
            .input(CMaterials.industrialClay)
            .output(CMaterials.carbon).tier(7)
            .chain(CMaterials.graphite).tier(8)
            .chain(CMaterials.charcoal).tier(9)
            .chain(CMaterials.coal).tier(10)
            .chain(CMaterials.lapis).tier(10)
            .chain(CMaterials.lazurite).tier(10)
            .chain(CMaterials.sodalite).tier(10).input(CMaterials.sodalite, 4)
            .chain(CMaterials.monazite).tier(11)
            .buildAndRegister()

        matterTransformer.builder()
            .defaultPrefix(OrePrefix.gem).duration(200)
            .input(CMaterials.diamond)
            .chain(CMaterials.amber).tier(10)
            .chain(CMaterials.amethyst)
            .chain(CMaterials.peridot)
            .chain(CMaterials.sapphire)
            .chain(CMaterials.ruby)
            .chain(CMaterials.emerald).tier(11)
            .buildAndRegister()

        matterTransformer.builder()
            .input(Items.FLINT)
            .output(OrePrefix.gem, CMaterials.cinnabar)
            .tier(10).duration(1000)
            .buildAndRegister()

        val flour = OreDictUnifier.get("flour").takeUnless { it.isEmpty }
            ?: OreDictUnifier.get("itemFlour").takeUnless { it.isEmpty }
            ?: OreDictUnifier.get("dustFlour").takeUnless { it.isEmpty }

        if (flour != null) {
            CRecipes.GRINDER.builder()
                .input(Items.WHEAT)
                .output(flour)
                .tier(5).duration(60)
                .buildAndRegister()
        }

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