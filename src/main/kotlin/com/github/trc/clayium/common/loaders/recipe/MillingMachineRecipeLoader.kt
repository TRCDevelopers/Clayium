package com.github.trc.clayium.common.loaders.recipe

import com.github.trc.clayium.api.ClayEnergy
import com.github.trc.clayium.api.unification.material.CMaterials
import com.github.trc.clayium.api.unification.ore.OrePrefix
import com.github.trc.clayium.common.items.metaitem.MetaItemClayParts
import com.github.trc.clayium.common.recipe.registry.CRecipes

object MillingMachineRecipeLoader {
    fun registerRecipes() {
        val registry = CRecipes.MILLING_MACHINE

        registry
            .builder()
            .input(OrePrefix.plate, CMaterials.denseClay)
            .output(MetaItemClayParts.ClayCircuitBoard)
            .tier(0)
            .CEtByTier(0)
            .duration(32)
            .buildAndRegister()

        registry
            .builder()
            .input(OrePrefix.plate, CMaterials.industrialClay)
            .output(MetaItemClayParts.ClayCircuitBoard)
            .tier(0)
            .CEtByTier(0)
            .duration(1)
            .buildAndRegister()

        registry
            .builder()
            .input(OrePrefix.plate, CMaterials.advancedIndustrialClay)
            .output(MetaItemClayParts.CeeBoard)
            .tier(3)
            .CEt(ClayEnergy.micro(20))
            .duration(32)
            .buildAndRegister()
    }
}
