package com.github.trc.clayium.common.loaders.recipe

import com.github.trc.clayium.api.ClayEnergy
import com.github.trc.clayium.common.items.metaitem.MetaItemClayParts
import com.github.trc.clayium.common.recipe.registry.CRecipes
import com.github.trc.clayium.common.unification.material.CMaterials
import com.github.trc.clayium.common.unification.ore.OrePrefix

object CondenserRecipeLoader {
    fun registerRecipes() {
        val registry = CRecipes.CONDENSER

        registry.builder()
            .input(MetaItemClayParts.COMPRESSED_CLAY_SHARD, 4)
            .output(OrePrefix.block, CMaterials.compressedClay)
            .duration(3)
            .buildAndRegister()
        registry.builder()
            .input(MetaItemClayParts.INDUSTRIAL_CLAY_SHARD, 4)
            .output(OrePrefix.block, CMaterials.industrialClay)
            .duration(6)
            .buildAndRegister()
        registry.builder()
            .input(MetaItemClayParts.ADV_INDUSTRIAL_CLAY_SHARD, 4)
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