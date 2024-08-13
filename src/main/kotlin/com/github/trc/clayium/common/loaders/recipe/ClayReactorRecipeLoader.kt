package com.github.trc.clayium.common.loaders.recipe

import com.github.trc.clayium.api.ClayEnergy
import com.github.trc.clayium.common.items.metaitem.MetaItemClayParts
import com.github.trc.clayium.common.recipe.registry.CRecipes
import com.github.trc.clayium.common.unification.material.CMaterials
import com.github.trc.clayium.common.unification.ore.OrePrefix
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
            .input(MetaItemClayParts.EnergizedClayDust, 8)
            .input(OrePrefix.dust, CMaterials.lithium)
            .output(MetaItemClayParts.ExcitedClayDust, 4)
            .tier(7).CEt(1.0).duration(2_000_000)
            .buildAndRegister()

        registry.builder()
            .input(MetaItemClayParts.ClaySoul)
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
            .output(MetaItemClayParts.AntimatterSeed)
            .tier(9).CEt(1.0).duration(200_000_000_000_000)
            .buildAndRegister()

        /* Circuit Recipes */
        registry.builder()
            .input(MetaItemClayParts.IntegratedCircuit, 6)
            .input(MetaItemClayParts.ExcitedClayDust)
            .output(MetaItemClayParts.ClayCore)
            .tier(7).CEt(10.0).duration(8_000_000)
            .buildAndRegister()
        registry.builder()
            .input(MetaItemClayParts.ClayCore, 6)
            .input(MetaItemClayParts.ExcitedClayDust, 12)
            .output(MetaItemClayParts.ClayBrain)
            .tier(8).CEt(10.0).duration(4_000_000_000)
            .buildAndRegister()
        registry.builder()
            .input(MetaItemClayParts.ClayBrain, 6)
            .input(MetaItemClayParts.ExcitedClayDust, 32)
            .output(MetaItemClayParts.ClaySpirit)
            .tier(9).CEt(10.0).duration(10_000_000_000_000)
            .buildAndRegister()
        registry.builder()
            .input(MetaItemClayParts.ClaySpirit, 6)
            .input(OrePrefix.gem, CMaterials.antimatter, 4)
            .output(MetaItemClayParts.ClaySoul)
            .tier(10).CEt(10.0).duration(10_000_000_000_000)
            .buildAndRegister()
        registry.builder()
            .input(MetaItemClayParts.ClaySoul, 6)
            .input(OrePrefix.gem, CMaterials.antimatter, 16)
            .output(MetaItemClayParts.ClayAnima)
            .tier(11).CEt(30.0).duration(100_000_000_000_000)
            .buildAndRegister()
        registry.builder()
            .input(MetaItemClayParts.ClayAnima, 6)
            .input(OrePrefix.gem, CMaterials.antimatter, 64)
            .output(MetaItemClayParts.ClayPsyche)
            .tier(12).CEt(90.0).duration(1_000_000_000_000_000)
            .buildAndRegister()

        /* Clay Parts */
        registry.builder()
            .input(MetaItemClayParts.IntegratedCircuit)
            .input(OrePrefix.gem, CMaterials.pureAntimatter, 8)
            .output(MetaItemClayParts.TeleportationParts)
            .tier(11).duration(10_00_000_000_000)
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

        registry.builder()
            .input(Blocks.GRAVEL)
            .input(OrePrefix.dust, CMaterials.organicClay)
            .output(Blocks.DIRT)
            .tier(7).CEt(ClayEnergy.of(1)).duration(100)
            .buildAndRegister()

        registry.builder()
            .input(OrePrefix.gem, CMaterials.antimatter)
            .output(OrePrefix.gem, CMaterials.pureAntimatter)
            .tier(10).CEt(ClayEnergy.of(100)).duration(300)
            .buildAndRegister()
    }
}