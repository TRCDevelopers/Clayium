package com.github.trc.clayium.common.loaders.recipe

import com.github.trc.clayium.api.unification.material.CMaterials
import com.github.trc.clayium.api.unification.ore.OrePrefix
import com.github.trc.clayium.common.blocks.ClayiumBlocks
import com.github.trc.clayium.common.items.metaitem.MetaItemClayParts
import com.github.trc.clayium.common.recipe.registry.CRecipes

object GrinderRecipeLoader {
    fun registerRecipes() {
        val registry = CRecipes.GRINDER

        registry.builder()
            .input(ClayiumBlocks.CLAY_ORE)
            .output(MetaItemClayParts.CompressedClayShard, 2)
            .duration(3)
            .buildAndRegister()
        registry.builder()
            .input(ClayiumBlocks.DENSE_CLAY_ORE)
            .output(MetaItemClayParts.IndustrialClayShard, 3)
            .duration(6)
            .buildAndRegister()
        registry.builder()
            .input(ClayiumBlocks.LARGE_DENSE_CLAY_ORE)
            .output(MetaItemClayParts.AdvancedIndustrialClayShard, 5)
            .duration(9)
            .buildAndRegister()
        registry.builder()
            .input(ClayiumBlocks.CLAY_TREE_LOG)
            .output(OrePrefix.dust, CMaterials.organicClay)
            .tier(5).defaultCEt().duration(200)
            .buildAndRegister()
    }
}