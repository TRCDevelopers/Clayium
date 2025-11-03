package io.github.trcdevelopers.clayium.common.loaders.recipe.integration.tconstruct

import io.github.trcdevelopers.clayium.api.unification.OreDictUnifier
import io.github.trcdevelopers.clayium.api.unification.material.CMarkerMaterials
import io.github.trcdevelopers.clayium.api.unification.material.CMaterials
import io.github.trcdevelopers.clayium.api.unification.ore.OrePrefix
import io.github.trcdevelopers.clayium.common.recipe.registry.CRecipes
import net.minecraft.init.Blocks

object TConstructRecipeLoader {
    fun registerRecipes() {
        CRecipes.CA_INJECTOR.builder()
            .input(OrePrefix.ingot, CMaterials.cobalt)
            .input(OrePrefix.gem, CMaterials.antimatter)
            .output(OrePrefix.ingot, CMarkerMaterials.ardite)
            .tier(10).duration(60)
            .buildAndRegister()

        val alloySmelter = CRecipes.ALLOY_SMELTER
        val reactor = CRecipes.CLAY_REACTOR

        val ingotDust = listOf(OrePrefix.ingot, OrePrefix.dust)
        alloySmelter.builder()
            .input(CMaterials.aluminum, 3)
            .input(CMaterials.copper)
            .output(OrePrefix.ingot, CMarkerMaterials.aluminumBrass, 4)
            .tier(6).duration(100)
            .buildAndRegister()
        alloySmelter.builder()
            .input(CMarkerMaterials.ardite)
            .input(CMaterials.cobalt)
            .output(OrePrefix.ingot, CMarkerMaterials.manyullyn)
            .tier(6).duration(100)
            .buildAndRegister()

        reactor.builder()
            .input(ingotDust, CMaterials.iron)
            .input(OrePrefix.gem, CMarkerMaterials.emerald, 9)
            .output(OrePrefix.ingot, CMarkerMaterials.pigiron)
            .tier(9).duration(100_000_000_000)
            .buildAndRegister()

        if (OreDictUnifier.exists(OrePrefix.ingot, CMarkerMaterials.pokefennium)) {
            reactor.builder()
                .input(ingotDust, CMaterials.iron)
                .input(ingotDust, CMaterials.cobalt)
                .output(OrePrefix.ingot, CMarkerMaterials.pokefennium, 2)
                .tier(8).duration(1_000_000_000)
                .buildAndRegister()
        }

        if (OreDictUnifier.exists(OrePrefix.ingot, CMarkerMaterials.darkSteel)
            || OreDictUnifier.exists(OrePrefix.dust, CMarkerMaterials.darkSteel)) {
            reactor.builder()
                .input(ingotDust, CMaterials.aluminum)
                .input(ingotDust, CMarkerMaterials.darkSteel, 2)
                .output(OrePrefix.ingot, CMarkerMaterials.alumite, 3)
                .tier(8).duration(1_000_000_000)
                .buildAndRegister()
        }

        if (OreDictUnifier.exists(OrePrefix.ingot, CMarkerMaterials.fairy)) {
            reactor.builder()
                .input(ingotDust, CMarkerMaterials.ardite)
                .input(Blocks.OBSIDIAN)
                .output(OrePrefix.ingot, CMarkerMaterials.fairy, 2)
                .tier(8).duration(1_000_000_000)
                .buildAndRegister()
        }
    }
}