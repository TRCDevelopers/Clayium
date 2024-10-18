package com.github.trc.clayium.common.loaders.recipe

import com.github.trc.clayium.api.ClayEnergy
import com.github.trc.clayium.api.unification.material.CMarkerMaterials
import com.github.trc.clayium.api.unification.material.CMaterials
import com.github.trc.clayium.api.unification.ore.OrePrefix
import com.github.trc.clayium.api.util.Mods
import com.github.trc.clayium.common.blocks.ClayiumBlocks
import com.github.trc.clayium.common.recipe.registry.CRecipes
import net.minecraft.init.Blocks
import net.minecraft.init.Items

object ClayBlastFurnaceRecipeLoader {
    fun registerRecipes() {
        val registry = CRecipes.CLAY_BLAST_FURNACE

        registry
            .builder()
            .input(OrePrefix.dust, CMaterials.advancedIndustrialClay)
            .input(OrePrefix.ingot, CMaterials.impureSilicon)
            .output(OrePrefix.ingot, CMaterials.silicon)
            .tier(7)
            .duration(100)
            .buildAndRegister()

        registry
            .builder()
            .input(OrePrefix.dust, CMaterials.industrialClay, 2)
            .input(OrePrefix.impureDust, CMaterials.manganese)
            .output(OrePrefix.ingot, CMaterials.claySteel, 2)
            .tier(6)
            .CEtFactor(5.0)
            .duration(200)
            .buildAndRegister()

        registry
            .builder()
            .input(OrePrefix.dust, CMaterials.advancedIndustrialClay)
            .input(OrePrefix.impureDust, CMaterials.manganese)
            .output(OrePrefix.ingot, CMaterials.claySteel)
            .tier(7)
            .CEtFactor(5.0)
            .duration(5)
            .buildAndRegister()

        registry
            .builder()
            .input(OrePrefix.dust, CMaterials.advancedIndustrialClay)
            .input(OrePrefix.dust, CMaterials.manganese)
            .output(OrePrefix.ingot, CMaterials.claySteel)
            .tier(8)
            .CEtFactor(5.0)
            .duration(1)
            .buildAndRegister()

        registry
            .builder()
            .input(OrePrefix.dust, CMaterials.industrialClay)
            .input(OrePrefix.dust, CMaterials.quartz, 8)
            .output(ClayiumBlocks.LASER_REFLECTOR)
            .tier(7)
            .CEtFactor(2.0)
            .duration(100)
            .buildAndRegister()

        registry
            .builder()
            .input(Items.IRON_INGOT)
            .input(Items.COAL, 2)
            .output(OrePrefix.ingot, CMaterials.steel)
            .tier(6)
            .CEt(ClayEnergy.milli(100))
            .duration(500)
            .buildAndRegister()
        if (Mods.EnderIO.isModLoaded) {
            /* Electrical Steel */
            registry
                .builder()
                .input(arrayOf(OrePrefix.ingot, OrePrefix.dust), CMaterials.steel)
                .input(OrePrefix.item, CMaterials.silicon)
                .output(OrePrefix.ingot, CMarkerMaterials.electricalSteel)
                .tier(7)
                .defaultCEt()
                .duration(500)
                .buildAndRegister()
            /* Dark Steel */
            registry
                .builder()
                .input(arrayOf(OrePrefix.ingot, OrePrefix.dust), CMaterials.steel)
                .input(Blocks.OBSIDIAN)
                .output(OrePrefix.ingot, CMarkerMaterials.darkSteel)
                .tier(7)
                .defaultCEt()
                .duration(500)
                .buildAndRegister()
            /* Pulsating Iron */
            registry
                .builder()
                .input(arrayOf(OrePrefix.ingot, OrePrefix.dust), CMaterials.iron)
                .input(Items.ENDER_PEARL)
                .output(OrePrefix.ingot, CMarkerMaterials.pulsatingIron)
                .tier(6)
                .defaultCEt()
                .duration(500)
                .buildAndRegister()
            /* Vibrant Alloy */
            registry
                .builder()
                .input(arrayOf(OrePrefix.ingot, OrePrefix.dust), CMarkerMaterials.energeticAlloy)
                .input(Items.ENDER_PEARL)
                .output(OrePrefix.ingot, CMarkerMaterials.vibrantAlloy)
                .tier(6)
                .defaultCEt()
                .duration(500)
                .buildAndRegister()
            /* Soularium Ingot */
            registry
                .builder()
                .input(arrayOf(OrePrefix.ingot, OrePrefix.dust), CMaterials.gold)
                .input(Blocks.SOUL_SAND)
                .output(OrePrefix.ingot, CMarkerMaterials.soularium)
                .tier(6)
                .defaultCEt()
                .duration(500)
                .buildAndRegister()
        }
    }
}
