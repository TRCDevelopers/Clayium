package com.github.trc.clayium.common.loaders.recipe

import com.github.trc.clayium.api.ClayEnergy
import com.github.trc.clayium.api.unification.material.CMaterials
import com.github.trc.clayium.api.unification.ore.OrePrefix
import com.github.trc.clayium.api.util.ClayTiers
import com.github.trc.clayium.common.blocks.BlockCaReactorCoil
import com.github.trc.clayium.common.blocks.ClayiumBlocks
import com.github.trc.clayium.common.items.metaitem.MetaItemClayParts
import com.github.trc.clayium.common.recipe.registry.CRecipes
import net.minecraft.init.Blocks
import net.minecraft.init.Items

object ClayReactorRecipeLoader {
    fun registerRecipes() {
        val registry = CRecipes.CLAY_REACTOR

        registry.builder()
            .input(OrePrefix.dust, CMaterials.advancedIndustrialClay, 8)
            .input(OrePrefix.dust, CMaterials.lithium, 4)
            .output(OrePrefix.dust, CMaterials.clayium, 8)
            .tier(7).CEtFactor(10.0).duration(50_000)
            .buildAndRegister()

        registry.builder()
            .input(OrePrefix.dust, CMaterials.advancedIndustrialClay, 8)
            .input(OrePrefix.dust, CMaterials.hafnium)
            .output(OrePrefix.dust, CMaterials.clayium, 8)
            .tier(7).CEtFactor(10.0).duration(500_000)
            .buildAndRegister()

        registry.builder()
            .input(OrePrefix.dust, CMaterials.advancedIndustrialClay, 8)
            .input(OrePrefix.dust, CMaterials.barium)
            .output(OrePrefix.dust, CMaterials.clayium, 8)
            .tier(7).CEtFactor(3.0).duration(5_000_000)
            .buildAndRegister()

        registry.builder()
            .input(OrePrefix.dust, CMaterials.advancedIndustrialClay, 8)
            .input(OrePrefix.dust, CMaterials.strontium)
            .output(OrePrefix.dust, CMaterials.clayium, 8)
            .tier(7).CEtFactor(1.0).duration(50_000_000)
            .buildAndRegister()

        registry.builder()
            .input(OrePrefix.dust, CMaterials.advancedIndustrialClay)
            .input(OrePrefix.ingot, CMaterials.ultimateCompound)
            .output(OrePrefix.ingot, CMaterials.ultimateAlloy)
            .tier(8).CEtFactor(10.0).duration(1_000_000_000)
            .buildAndRegister()

        registry.builder()
            .input(MetaItemClayParts.EnergizedClayDust, 8)
            .input(OrePrefix.dust, CMaterials.lithium)
            .output(MetaItemClayParts.ExcitedClayDust, 4)
            .tier(7).CEtFactor(1.0).duration(2_000_000)
            .buildAndRegister()

        registry.builder()
            .input(MetaItemClayParts.ClaySoul)
            .input(OrePrefix.dust, CMaterials.advancedIndustrialClay)
            .output(OrePrefix.dust, CMaterials.organicClay, 2)
            .tier(11).CEtFactor(1.0).duration(1_000_000_000_000)
            .buildAndRegister()

        registry.builder()
            .input(OrePrefix.dust, CMaterials.organicClay)
            .input(OrePrefix.dust, CMaterials.advancedIndustrialClay)
            .output(OrePrefix.dust, CMaterials.organicClay, 2)
            .tier(10).CEtFactor(1.0).duration(100_000_000_000_000)
            .buildAndRegister()

        registry.builder()
            .input(OrePrefix.ingot, CMaterials.clayium)
            .output(MetaItemClayParts.AntimatterSeed)
            .tier(9).CEtFactor(1.0).duration(200_000_000_000_000)
            .buildAndRegister()

        /* Circuit Recipes */
        registry.builder()
            .input(MetaItemClayParts.IntegratedCircuit, 6)
            .input(MetaItemClayParts.ExcitedClayDust)
            .output(MetaItemClayParts.ClayCore)
            .tier(7).CEtFactor(10.0).duration(8_000_000)
            .buildAndRegister()
        registry.builder()
            .input(MetaItemClayParts.ClayCore, 6)
            .input(MetaItemClayParts.ExcitedClayDust, 12)
            .output(MetaItemClayParts.ClayBrain)
            .tier(8).CEtFactor(10.0).duration(4_000_000_000)
            .buildAndRegister()
        registry.builder()
            .input(MetaItemClayParts.ClayBrain, 6)
            .input(MetaItemClayParts.ExcitedClayDust, 32)
            .output(MetaItemClayParts.ClaySpirit)
            .tier(9).CEtFactor(10.0).duration(10_000_000_000_000)
            .buildAndRegister()
        registry.builder()
            .input(MetaItemClayParts.ClaySpirit, 6)
            .input(OrePrefix.gem, CMaterials.antimatter, 4)
            .output(MetaItemClayParts.ClaySoul)
            .tier(10).CEtFactor(10.0).duration(10_000_000_000_000)
            .buildAndRegister()
        registry.builder()
            .input(MetaItemClayParts.ClaySoul, 6)
            .input(OrePrefix.gem, CMaterials.antimatter, 16)
            .output(MetaItemClayParts.ClayAnima)
            .tier(11).CEtFactor(30.0).duration(100_000_000_000_000)
            .buildAndRegister()
        registry.builder()
            .input(MetaItemClayParts.ClayAnima, 6)
            .input(OrePrefix.gem, CMaterials.antimatter, 64)
            .output(MetaItemClayParts.ClayPsyche)
            .tier(12).CEtFactor(90.0).duration(1_000_000_000_000_000)
            .buildAndRegister()

        /* Clay Parts */
        registry.builder()
            .input(MetaItemClayParts.IntegratedCircuit)
            .input(OrePrefix.gem, CMaterials.pureAntimatter, 8)
            .output(MetaItemClayParts.TeleportationParts)
            .tier(11).duration(10_00_000_000_000)
            .buildAndRegister()

        /* Misc Recipes */
        registry.builder()
            .input(OrePrefix.dust, CMaterials.impureRedstone)
            .output(Items.REDSTONE)
            .tier(7).CEtFactor(0.1).duration(2000)
            .buildAndRegister()

        registry.builder()
            .input(OrePrefix.dust, CMaterials.impureGlowStone)
            .output(Items.GLOWSTONE_DUST)
            .tier(7).CEtFactor(0.1).duration(2000)
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

        /* Overclocker */
        registry.builder()
            .input(ClayiumBlocks.MACHINE_HULL.getItem(ClayTiers.ANTIMATTER))
            .input(ClayiumBlocks.RESONATOR.getItem(BlockCaReactorCoil.BlockType.ANTIMATTER, 8))
            .output(ClayiumBlocks.OVERCLOCKER.getItem(BlockCaReactorCoil.BlockType.ANTIMATTER))
            .tier(10).CEtFactor(5.0).duration(10_000_000_000_000)
            .buildAndRegister()
        registry.builder()
            .input(ClayiumBlocks.MACHINE_HULL.getItem(ClayTiers.PURE_ANTIMATTER, 4))
            .input(ClayiumBlocks.RESONATOR.getItem(BlockCaReactorCoil.BlockType.PURE_ANTIMATTER, 16))
            .output(ClayiumBlocks.OVERCLOCKER.getItem(BlockCaReactorCoil.BlockType.PURE_ANTIMATTER))
            .tier(11).CEtFactor(5.0).duration(100_000_000_000_000)
            .buildAndRegister()
        registry.builder()
            .input(ClayiumBlocks.MACHINE_HULL.getItem(ClayTiers.OEC, 16))
            .input(ClayiumBlocks.RESONATOR.getItem(BlockCaReactorCoil.BlockType.OEC, 32))
            .output(ClayiumBlocks.OVERCLOCKER.getItem(BlockCaReactorCoil.BlockType.OEC))
            .tier(12).CEtFactor(5.0).duration(1_000_000_000_000_000)
            .buildAndRegister()
        registry.builder()
            .input(ClayiumBlocks.MACHINE_HULL.getItem(ClayTiers.OPA, 64))
            .input(ClayiumBlocks.RESONATOR.getItem(BlockCaReactorCoil.BlockType.OPA, 64))
            .output(ClayiumBlocks.OVERCLOCKER.getItem(BlockCaReactorCoil.BlockType.OPA))
            .tier(13).CEtFactor(5.0).duration(1_000_000_000_000_000)
            .buildAndRegister()
    }
}