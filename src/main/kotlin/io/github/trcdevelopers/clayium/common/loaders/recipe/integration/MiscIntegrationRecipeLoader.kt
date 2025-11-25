package io.github.trcdevelopers.clayium.common.loaders.recipe.integration

import io.github.trcdevelopers.clayium.api.unification.OreDictUnifier
import io.github.trcdevelopers.clayium.api.unification.material.CMarkerMaterials
import io.github.trcdevelopers.clayium.api.unification.material.CMaterials
import io.github.trcdevelopers.clayium.api.unification.ore.OrePrefix
import io.github.trcdevelopers.clayium.api.util.VItems
import io.github.trcdevelopers.clayium.common.recipe.registry.CRecipes
import net.minecraft.init.Blocks
import net.minecraft.init.Items

object MiscIntegrationRecipeLoader {

    fun registerMandatory() {
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
    }

    fun registerRecipes() {
        registerCropRecipes()
        val caInjector = CRecipes.CA_INJECTOR
        val reactor = CRecipes.CLAY_REACTOR
        val matterTransformer = CRecipes.MATTER_TRANSFORMER

        matterTransformer.builder()
            .input(Items.FLINT)
            .output(OrePrefix.gem, CMaterials.cinnabar)
            .tier(10).duration(1000)
            .buildAndRegister()

        if (OreDictUnifier.exists(OrePrefix.item, CMaterials.rawRubber)) {
            caInjector.builder()
                .input(OrePrefix.log, CMaterials.wood)
                .inputAntimatter(1)
                .output(OrePrefix.item, CMaterials.rawRubber)
                .tier(10).CEtFactor(2.0).duration(60)
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

    private fun registerCropRecipes() {
        val reactor = CRecipes.CLAY_REACTOR

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

        if (OreDictUnifier.exists(OrePrefix.seed, CMaterials.cotton)) {
            reactor.builder()
                .input(Items.MELON_SEEDS)
                .input(Items.STRING, 3)
                .output(OrePrefix.seed, CMaterials.cotton)
                .tier(8).duration(1_000_000_000)
                .buildAndRegister()

        }

        if (OreDictUnifier.exists(OrePrefix.crop, CMaterials.rice)) {
            reactor.builder()
                .input(Items.WHEAT_SEEDS)
                .input(Items.WHEAT)
                .output(OrePrefix.crop, CMaterials.rice)
                .tier(8).duration(1_000_000_000)
                .buildAndRegister()
        }

        if (OreDictUnifier.exists(OrePrefix.crop, CMaterials.straw)) {
            reactor.builder()
                .input(Items.WHEAT)
                .input(Blocks.TALLGRASS)
                .output(OrePrefix.crop, CMaterials.straw)
                .tier(8).duration(1_000_000_000)
                .buildAndRegister()
        }

        if (OreDictUnifier.exists(OrePrefix.crop, CMaterials.coffee)) {
            reactor.builder()
                .input(Items.WHEAT_SEEDS)
                .input(VItems.COCOA_BEANS)
                .output(OrePrefix.crop, CMaterials.coffee)
                .tier(10).duration(100_000_000_000_000)
                .buildAndRegister()
        }

        if (OreDictUnifier.exists("apricot")) {
            reactor.builder()
                .input(Items.APPLE)
                .input(OrePrefix.dye, CMarkerMaterials.red)
                .output("apricot")
                .tier(8).duration(1_000_000_000)
                .buildAndRegister()
        }

        if (OreDictUnifier.exists("bamboo")) {
            reactor.builder()
                .input(Items.REEDS)
                .input(OrePrefix.log, CMaterials.wood)
                .output("bamboo")
                .tier(8).duration(1_000_000_000)
                .buildAndRegister()
        }
    }
}