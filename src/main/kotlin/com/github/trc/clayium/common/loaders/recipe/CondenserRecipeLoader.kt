package com.github.trc.clayium.common.loaders.recipe

import com.github.trc.clayium.api.ClayEnergy
import com.github.trc.clayium.api.unification.material.CMaterials
import com.github.trc.clayium.api.unification.ore.OrePrefix
import com.github.trc.clayium.common.items.metaitem.MetaItemClayParts
import com.github.trc.clayium.common.recipe.registry.CRecipes

object CondenserRecipeLoader {
    fun registerRecipes() {
        val registry = CRecipes.CONDENSER

        registry.builder()
            .input(MetaItemClayParts.CompressedClayShard, 4)
            .output(OrePrefix.block, CMaterials.compressedClay)
            .duration(3)
            .buildAndRegister()
        registry.builder()
            .input(MetaItemClayParts.IndustrialClayShard, 4)
            .output(OrePrefix.block, CMaterials.industrialClay)
            .duration(6)
            .buildAndRegister()
        registry.builder()
            .input(MetaItemClayParts.AdvancedIndustrialClayShard, 4)
            .output(OrePrefix.block, CMaterials.advancedIndustrialClay)
            .duration(9)
            .buildAndRegister()

        for (i in 0..<(CMaterials.PURE_ANTIMATTERS.size - 1)) {
            registry.builder()
                .input(OrePrefix.gem, CMaterials.PURE_ANTIMATTERS[i], 9)
                .output(OrePrefix.gem, CMaterials.PURE_ANTIMATTERS[i + 1])
                .tier(10).CEt(ClayEnergy.of(100)).duration(6)
                .buildAndRegister()
        }
    }
}