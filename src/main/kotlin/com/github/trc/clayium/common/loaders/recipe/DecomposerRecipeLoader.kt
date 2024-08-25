package com.github.trc.clayium.common.loaders.recipe

import com.github.trc.clayium.api.ClayEnergy
import com.github.trc.clayium.api.unification.material.CMaterials
import com.github.trc.clayium.api.unification.ore.OrePrefix
import com.github.trc.clayium.common.items.metaitem.MetaItemClayParts
import com.github.trc.clayium.common.recipe.registry.CRecipes

object DecomposerRecipeLoader {
    fun registerRecipes() {
        val registry = CRecipes.DECOMPOSER

        registry.builder()
            .input(OrePrefix.dust, CMaterials.industrialClay)
            .output(MetaItemClayParts.EnergizedClayDust, 3)
            .tier(0).duration(60)
            .buildAndRegister()

        registry.builder()
            .input(OrePrefix.dust, CMaterials.advancedIndustrialClay)
            .output(MetaItemClayParts.EnergizedClayDust, 28)
            .tier(4).CEt(ClayEnergy.milli(10)).duration(60)
            .buildAndRegister()
    }
}