package com.github.trcdevelopers.clayium.common.loaders.recipe

import com.github.trcdevelopers.clayium.common.clayenergy.ClayEnergy
import com.github.trcdevelopers.clayium.common.items.metaitem.MetaItemClayParts
import com.github.trcdevelopers.clayium.common.recipe.registry.CRecipes
import com.github.trcdevelopers.clayium.common.unification.material.CMaterials
import com.github.trcdevelopers.clayium.common.unification.ore.OrePrefix

object MillingMachineRecipeLoader {
    fun registerRecipes() {
        val registry = CRecipes.MILLING_MACHINE

        registry.builder()
            .input(OrePrefix.plate, CMaterials.denseClay)
            .output(MetaItemClayParts.CLAY_CIRCUIT_BOARD)
            .tier(0).CEt().duration(32)
            .buildAndRegister()

        registry.builder()
            .input(OrePrefix.plate, CMaterials.industrialClay)
            .output(MetaItemClayParts.CLAY_CIRCUIT_BOARD)
            .tier(0).CEt().duration(1)
            .buildAndRegister()

        registry.builder()
            .input(OrePrefix.plate, CMaterials.advancedIndustrialClay)
            .output(MetaItemClayParts.CEE_BOARD)
            .tier(3).CEt(ClayEnergy.micro(20)).duration(32)
            .buildAndRegister()
    }
}