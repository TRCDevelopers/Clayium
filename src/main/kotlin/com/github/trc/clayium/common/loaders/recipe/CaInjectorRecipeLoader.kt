package com.github.trc.clayium.common.loaders.recipe

import com.github.trc.clayium.api.unification.material.CMaterials
import com.github.trc.clayium.api.unification.ore.OrePrefix
import com.github.trc.clayium.api.util.ClayTiers
import com.github.trc.clayium.common.blocks.BlockCaReactorCoil
import com.github.trc.clayium.common.blocks.ClayiumBlocks
import com.github.trc.clayium.common.recipe.registry.CRecipes
import net.minecraft.item.ItemStack

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
    }
}