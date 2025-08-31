package io.github.trcdevelopers.clayium.common.loaders.recipe

import io.github.trcdevelopers.clayium.api.ClayEnergy
import io.github.trcdevelopers.clayium.api.unification.material.CMaterials
import io.github.trcdevelopers.clayium.api.unification.ore.OrePrefix
import io.github.trcdevelopers.clayium.common.items.metaitem.MetaItemClayParts
import io.github.trcdevelopers.clayium.common.recipe.registry.CRecipes

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