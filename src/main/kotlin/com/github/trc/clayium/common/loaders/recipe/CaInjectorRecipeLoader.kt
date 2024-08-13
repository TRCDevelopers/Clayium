package com.github.trc.clayium.common.loaders.recipe

import com.github.trc.clayium.api.util.ClayTiers
import com.github.trc.clayium.common.blocks.ClayiumBlocks
import com.github.trc.clayium.common.recipe.registry.CRecipes
import com.github.trc.clayium.common.unification.material.CMaterials
import com.github.trc.clayium.common.unification.ore.OrePrefix
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
    }
}