package com.github.trcdevelopers.clayium.common.loaders.recipe

import com.github.trcdevelopers.clayium.common.clayenergy.ClayEnergy
import com.github.trcdevelopers.clayium.common.items.metaitem.MetaItemClayParts
import com.github.trcdevelopers.clayium.common.recipe.registry.CRecipes
import com.github.trcdevelopers.clayium.common.unification.material.CMaterials
import com.github.trcdevelopers.clayium.common.unification.ore.OrePrefix

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