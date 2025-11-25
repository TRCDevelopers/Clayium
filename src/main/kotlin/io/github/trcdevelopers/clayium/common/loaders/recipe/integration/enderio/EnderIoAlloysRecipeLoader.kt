package io.github.trcdevelopers.clayium.common.loaders.recipe.integration.enderio

import io.github.trcdevelopers.clayium.api.unification.material.CMarkerMaterials
import io.github.trcdevelopers.clayium.api.unification.material.CMaterials
import io.github.trcdevelopers.clayium.api.unification.ore.OrePrefix
import io.github.trcdevelopers.clayium.common.recipe.registry.CRecipes
import net.minecraft.init.Blocks
import net.minecraft.init.Items

object EnderIoAlloysRecipeLoader {
    fun registerRecipes() {
        this.registerAlloySmelterRecipes()
        this.registerBlastFurnaceRecipes()
        this.registerClayReactorRecipes()
    }

    private fun registerAlloySmelterRecipes() {
        val registry = CRecipes.ALLOY_SMELTER
        /* Redstone Alloy */
        registry.builder()
            .input(Items.REDSTONE)
            .input(OrePrefix.item, CMaterials.silicon)
            .output(OrePrefix.ingot, CMarkerMaterials.redstoneAlloy)
            .tier(6).defaultCEt().duration(100)
            .buildAndRegister()
        /* Conductive Iron */
        registry.builder()
            .input(Items.REDSTONE)
            .input(CMaterials.iron)
            .output(OrePrefix.ingot, CMarkerMaterials.conductiveIron)
            .tier(6).defaultCEt().duration(100)
            .buildAndRegister()
    }

    private fun registerBlastFurnaceRecipes() {
        val registry = CRecipes.CLAY_BLAST_FURNACE

        /* Electrical Steel */
        registry.builder()
            .input(CMaterials.steel)
            .input(OrePrefix.item, CMaterials.silicon)
            .output(OrePrefix.ingot, CMarkerMaterials.electricalSteel)
            .tier(7).defaultCEt().duration(500)
            .buildAndRegister()
        /* Dark Steel */
        registry.builder()
            .input(CMaterials.steel)
            .input(Blocks.OBSIDIAN)
            .output(OrePrefix.ingot, CMarkerMaterials.darkSteel)
            .tier(7).defaultCEt().duration(500)
            .buildAndRegister()
        /* Pulsating Iron */
        registry.builder()
            .input(CMaterials.iron)
            .input(Items.ENDER_PEARL)
            .output(OrePrefix.ingot, CMarkerMaterials.pulsatingIron)
            .tier(6).defaultCEt().duration(500)
            .buildAndRegister()
        /* Vibrant Alloy */
        registry.builder()
            .input(CMarkerMaterials.energeticAlloy)
            .input(Items.ENDER_PEARL)
            .output(OrePrefix.ingot, CMarkerMaterials.vibrantAlloy)
            .tier(6).defaultCEt().duration(500)
            .buildAndRegister()
        /* Soularium Ingot */
        registry.builder()
            .input(CMaterials.gold)
            .input(Blocks.SOUL_SAND)
            .output(OrePrefix.ingot, CMarkerMaterials.soularium)
            .tier(6).defaultCEt().duration(500)
            .buildAndRegister()
    }

    private fun registerClayReactorRecipes() {
        val registry = CRecipes.CLAY_REACTOR

        /* Energetic Alloy */
        registry.builder()
            .input(Items.REDSTONE)
            .input(listOf(OrePrefix.ingot, OrePrefix.dust), CMaterials.gold)
            .output(OrePrefix.ingot, CMarkerMaterials.energeticAlloy)
            .tier(8).defaultCEt().duration(1_000_000_000)
            .buildAndRegister()
    }
}