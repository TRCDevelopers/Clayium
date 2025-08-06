package io.github.trcdevelopers.clayium.common.loaders.recipe

import io.github.trcdevelopers.clayium.api.ClayEnergy
import io.github.trcdevelopers.clayium.api.unification.material.CMaterials
import io.github.trcdevelopers.clayium.api.unification.ore.OrePrefix
import io.github.trcdevelopers.clayium.common.items.metaitem.MetaItemClayParts
import io.github.trcdevelopers.clayium.common.recipe.registry.CRecipes

object MillingMachineRecipeLoader {
    fun registerRecipes() {
        val registry = CRecipes.MILLING_MACHINE

        registry.builder()
            .input(OrePrefix.plate, CMaterials.denseClay)
            .output(MetaItemClayParts.ClayCircuitBoard)
            .tier(0).CEtByTier(0).duration(32)
            .buildAndRegister()

        registry.builder()
            .input(OrePrefix.plate, CMaterials.industrialClay)
            .output(MetaItemClayParts.ClayCircuitBoard)
            .tier(0).CEtByTier(0).duration(1)
            .buildAndRegister()

        registry.builder()
            .input(OrePrefix.plate, CMaterials.advancedIndustrialClay)
            .output(MetaItemClayParts.CeeBoard)
            .tier(3).CEt(ClayEnergy.micro(20)).duration(32)
            .buildAndRegister()
    }
}