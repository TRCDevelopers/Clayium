package com.github.trcdevelopers.clayium.common.loaders.recipe

import com.github.trcdevelopers.clayium.common.clayenergy.ClayEnergy
import com.github.trcdevelopers.clayium.common.items.metaitem.MetaItemClayParts
import com.github.trcdevelopers.clayium.common.recipe.registry.CRecipes
import com.github.trcdevelopers.clayium.common.unification.material.CMaterials
import com.github.trcdevelopers.clayium.common.unification.ore.OrePrefix
import net.minecraft.init.Blocks
import net.minecraft.init.Items

object ClayReactorRecipeLoader {
    fun registerRecipes() {
        val registry = CRecipes.CLAY_REACTOR

        registry.builder()
            .input(OrePrefix.dust, CMaterials.advancedIndustrialClay, 8)
            .input(OrePrefix.dust, CMaterials.lithium, 4)
            .output(OrePrefix.dust, CMaterials.clayium, 8)
            .tier(7).CEt(10.0).duration(50_000)
            .buildAndRegister()

        registry.builder()
            .input(OrePrefix.dust, CMaterials.advancedIndustrialClay, 8)
            .input(OrePrefix.dust, CMaterials.hafnium)
            .output(OrePrefix.dust, CMaterials.clayium, 8)
            .tier(7).CEt(10.0).duration(500_000)
            .buildAndRegister()

        registry.builder()
            .input(OrePrefix.dust, CMaterials.advancedIndustrialClay, 8)
            .input(OrePrefix.dust, CMaterials.barium)
            .output(OrePrefix.dust, CMaterials.clayium, 8)
            .tier(7).CEt(3.0).duration(5_000_000)
            .buildAndRegister()

        registry.builder()
            .input(OrePrefix.dust, CMaterials.advancedIndustrialClay, 8)
            .input(OrePrefix.dust, CMaterials.strontium)
            .output(OrePrefix.dust, CMaterials.clayium, 8)
            .tier(7).CEt(1.0).duration(50_000_000)
            .buildAndRegister()

        registry.builder()
            .input(OrePrefix.dust, CMaterials.advancedIndustrialClay)
            .input(OrePrefix.ingot, CMaterials.impureUltimateAlloy)
            .output(OrePrefix.ingot, CMaterials.ultimateAlloy)
            .tier(8).CEt(10.0).duration(1_000_000_000)
            .buildAndRegister()

        registry.builder()
            .input(MetaItemClayParts.EnergeticClayDust, 8)
            .input(OrePrefix.dust, CMaterials.lithium)
            .output(MetaItemClayParts.ExcitedClayDust, 4)
            .tier(7).CEt(1.0).duration(2_000_000)
            .buildAndRegister()

        registry.builder()
            .input(MetaItemClayParts.CLAY_SOUL)
            .input(OrePrefix.dust, CMaterials.advancedIndustrialClay)
            .output(OrePrefix.dust, CMaterials.organicClay, 2)
            .tier(11).CEt(1.0).duration(1_000_000_000_000)
            .buildAndRegister()

        registry.builder()
            .input(OrePrefix.dust, CMaterials.organicClay)
            .input(OrePrefix.dust, CMaterials.advancedIndustrialClay)
            .output(OrePrefix.dust, CMaterials.organicClay, 2)
            .tier(10).CEt(1.0).duration(100_000_000_000_000)
            .buildAndRegister()

        registry.builder()
            .input(OrePrefix.ingot, CMaterials.clayium)
            .output(MetaItemClayParts.ANTIMATTER_SEED)
            .tier(9).CEt(1.0).duration(200_000_000_000_000)
            .buildAndRegister()

        /* Circuit Recipes */
        registry.builder()
            .input(MetaItemClayParts.INTEGRATED_CIRCUIT, 6)
            .input(MetaItemClayParts.ExcitedClayDust)
            .output(MetaItemClayParts.CLAY_CORE)
            .tier(7).CEt(10.0).duration(8_000_000)
            .buildAndRegister()

        registry.builder()
            .input(MetaItemClayParts.CLAY_CORE, 6)
            .input(MetaItemClayParts.ExcitedClayDust, 12)
            .output(MetaItemClayParts.CLAY_BRAIN)
            .tier(8).CEt(10.0).duration(4_000_000_000)
            .buildAndRegister()

        registry.builder()
            .input(MetaItemClayParts.CLAY_BRAIN, 6)
            .input(MetaItemClayParts.ExcitedClayDust, 32)
            .output(MetaItemClayParts.CLAY_SPIRIT)
            .tier(9).CEt(10.0).duration(10_000_000_000_000)
            .buildAndRegister()

        registry.builder()
            .input(MetaItemClayParts.CLAY_SPIRIT, 6)
            .input(OrePrefix.gem, CMaterials.antimatter, 4)
            .output(MetaItemClayParts.CLAY_SOUL)
            .tier(10).CEt(10.0).duration(10_000_000_000_000)
            .buildAndRegister()

        registry.builder()
            .input(MetaItemClayParts.CLAY_SOUL, 6)
            .input(OrePrefix.gem, CMaterials.antimatter, 16)
            .output(MetaItemClayParts.CLAY_ANIMA)
            .tier(11).CEt(30.0).duration(100_000_000_000_000)
            .buildAndRegister()

        registry.builder()
            .input(MetaItemClayParts.CLAY_ANIMA, 6)
            .input(OrePrefix.gem, CMaterials.antimatter, 64)
            .output(MetaItemClayParts.CLAY_PSYCHE)
            .tier(12).CEt(90.0).duration(1_000_000_000_000_000)
            .buildAndRegister()

        /* Clay Fabricator Recipes */
        //todo

        /* Misc Recipes */
        registry.builder()
            .input(OrePrefix.dust, CMaterials.impureRedstone)
            .output(Items.REDSTONE)
            .tier(7).CEt(0.1).duration(2000)
            .buildAndRegister()

        registry.builder()
            .input(OrePrefix.dust, CMaterials.impureGlowStone)
            .output(Items.REDSTONE)
            .tier(7).CEt(0.1).duration(2000)
            .buildAndRegister()

        CRecipes.CLAY_REACTOR.register {
            input(Blocks.GRAVEL)
            input(OrePrefix.dust, CMaterials.organicClay)
            output(Blocks.DIRT)
            CEt(ClayEnergy.of(1))
            duration(100)
        }
    }
}