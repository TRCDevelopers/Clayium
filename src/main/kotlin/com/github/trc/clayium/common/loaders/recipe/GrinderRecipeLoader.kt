package com.github.trc.clayium.common.loaders.recipe

import com.github.trc.clayium.common.blocks.ClayiumBlocks
import com.github.trc.clayium.common.items.metaitem.MetaItemClayParts
import com.github.trc.clayium.common.recipe.registry.CRecipes

object GrinderRecipeLoader {
    fun registerRecipes() {
        val registry = CRecipes.GRINDER

        registry.builder()
            .input(ClayiumBlocks.CLAY_ORE)
            .output(MetaItemClayParts.COMPRESSED_CLAY_SHARD, 2)
            .duration(3)
            .buildAndRegister()
        registry.builder()
            .input(ClayiumBlocks.DENSE_CLAY_ORE)
            .output(MetaItemClayParts.INDUSTRIAL_CLAY_SHARD, 3)
            .duration(6)
            .buildAndRegister()
        registry.builder()
            .input(ClayiumBlocks.LARGE_DENSE_CLAY_ORE)
            .output(MetaItemClayParts.ADV_INDUSTRIAL_CLAY_SHARD, 5)
            .duration(9)
            .buildAndRegister()
    }
}