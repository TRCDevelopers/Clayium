package com.github.trc.clayium.common.loaders.recipe

import com.github.trc.clayium.api.unification.material.CMaterials
import com.github.trc.clayium.api.unification.ore.OrePrefix
import com.github.trc.clayium.api.util.ClayTiers
import com.github.trc.clayium.common.blocks.BlockCaReactorCoil
import com.github.trc.clayium.common.blocks.ClayiumBlocks
import com.github.trc.clayium.common.metatileentities.CaInjectorMetaTileEntity
import com.github.trc.clayium.common.metatileentities.MetaTileEntities
import com.github.trc.clayium.common.recipe.registry.CRecipes
import net.minecraft.item.ItemStack
import kotlin.collections.component1
import kotlin.collections.component2
import kotlin.collections.sumOf
import kotlin.collections.windowed

object CaInjectorRecipeLoader {
    fun registerRecipes() {
        val registry = CRecipes.CA_INJECTOR

        /* Resonators */
        registry.builder()
            .input(ClayiumBlocks.MACHINE_HULL.getItem(ClayTiers.ULTIMATE))
            .input(OrePrefix.gem, CMaterials.antimatter, 8)
            .output(ItemStack(ClayiumBlocks.RESONATOR, 1, 0))
            .tier(9).CEtFactor(2.0).duration(4000)
            .buildAndRegister()
        registry.builder()
            .input(ItemStack(ClayiumBlocks.RESONATOR, 16, 0))
            .input(OrePrefix.gem, CMaterials.antimatter, 64)
            .output(ItemStack(ClayiumBlocks.RESONATOR, 1, 1))
            .tier(11).CEtFactor(2.0).duration(4000)
            .buildAndRegister()
        registry.builder()
            .input(ItemStack(ClayiumBlocks.RESONATOR, 16, 1))
            .input(OrePrefix.gem, CMaterials.antimatter, 64)
            .output(ItemStack(ClayiumBlocks.RESONATOR, 1, 2))
            .tier(12).CEtFactor(2.0).duration(4000)
            .buildAndRegister()
        registry.builder()
            .input(ItemStack(ClayiumBlocks.RESONATOR, 16, 2))
            .input(OrePrefix.gem, CMaterials.antimatter, 64)
            .output(ItemStack(ClayiumBlocks.RESONATOR, 1, 3))
            .tier(13).CEtFactor(2.0).duration(4000)
            .buildAndRegister()

        /* Energy Storage Upgrades */
        registry.builder()
            .input(ClayiumBlocks.OVERCLOCKER.getItem(BlockCaReactorCoil.BlockType.ANTIMATTER))
            .input(OrePrefix.gem, CMaterials.antimatter, 8)
            .output(ClayiumBlocks.ENERGY_STORAGE_UPGRADE.getItem(BlockCaReactorCoil.BlockType.ANTIMATTER))
            .tier(9).CEtFactor(2.0).duration(4000)
            .buildAndRegister()
        registry.builder()
            .input(ClayiumBlocks.ENERGY_STORAGE_UPGRADE.getItem(BlockCaReactorCoil.BlockType.ANTIMATTER, 16))
            .input(OrePrefix.gem, CMaterials.antimatter, 64)
            .output(ClayiumBlocks.ENERGY_STORAGE_UPGRADE.getItem(BlockCaReactorCoil.BlockType.PURE_ANTIMATTER))
            .tier(11).CEtFactor(2.0).duration(4000)
            .buildAndRegister()
        registry.builder()
            .input(ClayiumBlocks.ENERGY_STORAGE_UPGRADE.getItem(BlockCaReactorCoil.BlockType.PURE_ANTIMATTER, 16))
            .input(OrePrefix.gem, CMaterials.antimatter, 64)
            .output(ClayiumBlocks.ENERGY_STORAGE_UPGRADE.getItem(BlockCaReactorCoil.BlockType.OEC))
            .tier(12).CEtFactor(2.0).duration(4000)
            .buildAndRegister()
        registry.builder()
            .input(ClayiumBlocks.ENERGY_STORAGE_UPGRADE.getItem(BlockCaReactorCoil.BlockType.OEC, 16))
            .input(OrePrefix.gem, CMaterials.antimatter, 64)
            .output(ClayiumBlocks.ENERGY_STORAGE_UPGRADE.getItem(BlockCaReactorCoil.BlockType.OPA))
            .tier(13).CEtFactor(2.0).duration(4000)
            .buildAndRegister()

        val duration = CaInjectorMetaTileEntity.DURATION
        val ceFactor = CaInjectorMetaTileEntity.CE_FACTOR

        for (mteList in MetaTileEntities.mteLists) {
            for ((prev, next) in mteList.windowed(2)) {
                val recipeTier = next.tier.numeric
                val prevTier = prev.tier.numeric
                // offset 2 (tier 1->2 to tier 12->13)
                val antimatterAmount = ((prevTier + 1)..recipeTier).sumOf { CaInjectorMetaTileEntity.ANTIMATTER_AMOUNTS[it - 2] }
                        .coerceAtMost(64)
                CRecipes.CA_INJECTOR.builder()
                    .input(prev)
                    .input(OrePrefix.gem, CMaterials.antimatter, antimatterAmount)
                    .output(next)
                    .tier(recipeTier).duration(duration)
                    .CEtFactor(ceFactor)
                    .buildAndRegister()
            }
        }

        // special cases //
        /* Solar -> Clay Fabricator Mk1, CF Mk1 -> CF Mk2. no Mk2->Mk3 Recipe */
        registry.builder()
            .input(MetaTileEntities.SOLAR_CLAY_FABRICATOR[2])
            .input(OrePrefix.gem, CMaterials.antimatter, 8)
            .output(MetaTileEntities.CLAY_FABRICATOR[0])
            .tier(8).duration(duration)
            .CEtFactor(ceFactor)
            .buildAndRegister()

        registry.builder()
            .input(MetaTileEntities.CLAY_FABRICATOR[0])
            .input(OrePrefix.gem, CMaterials.antimatter, 10)
            .output(MetaTileEntities.CLAY_FABRICATOR[1])
            .tier(9).duration(duration)
            .CEtFactor(ceFactor)
            .buildAndRegister()
    }
}