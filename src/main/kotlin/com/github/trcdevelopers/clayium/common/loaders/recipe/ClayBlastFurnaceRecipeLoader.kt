package com.github.trcdevelopers.clayium.common.loaders.recipe

import com.github.trcdevelopers.clayium.common.blocks.ClayiumBlocks
import com.github.trcdevelopers.clayium.common.clayenergy.ClayEnergy
import com.github.trcdevelopers.clayium.common.recipe.registry.CRecipes
import com.github.trcdevelopers.clayium.common.unification.material.CMaterials
import com.github.trcdevelopers.clayium.common.unification.ore.OrePrefix
import net.minecraft.init.Items

object ClayBlastFurnaceRecipeLoader {
    fun registerRecipes() {
        val registry = CRecipes.CLAY_BLAST_FURNACE

        registry.builder()
            .input(OrePrefix.dust, CMaterials.advancedIndustrialClay)
            .input(OrePrefix.ingot, CMaterials.impureSilicon)
            .output(OrePrefix.ingot, CMaterials.silicon)
            .tier(7).duration(100)
            .buildAndRegister()

        registry.builder()
            .input(OrePrefix.dust, CMaterials.industrialClay, 2)
            .input(OrePrefix.impureDust, CMaterials.manganese)
            .output(OrePrefix.ingot, CMaterials.claySteel, 2)
            .tier(6).CEt(5.0).duration(200)
            .buildAndRegister()

        registry.builder()
            .input(OrePrefix.dust, CMaterials.advancedIndustrialClay)
            .input(OrePrefix.impureDust, CMaterials.manganese)
            .output(OrePrefix.ingot, CMaterials.claySteel)
            .tier(7).CEt(5.0).duration(5)
            .buildAndRegister()

        registry.builder()
            .input(OrePrefix.dust, CMaterials.advancedIndustrialClay)
            .input(OrePrefix.dust, CMaterials.manganese)
            .output(OrePrefix.ingot, CMaterials.claySteel)
            .tier(8).CEt(5.0).duration(1)
            .buildAndRegister()

        registry.builder()
            .input(OrePrefix.dust, CMaterials.industrialClay)
            .input(OrePrefix.dust, CMaterials.quartz, 8)
            .output(ClayiumBlocks.LASER_REFLECTOR)
            .tier(7).CEt(2.0).duration(100)
            .buildAndRegister()

        registry.builder()
            .input(Items.IRON_INGOT)
            .input(Items.COAL, 2)
            .output(OrePrefix.ingot, CMaterials.steel)
            .tier(6).CEt(ClayEnergy.milli(100)).duration(500)
            .buildAndRegister()
    }
}