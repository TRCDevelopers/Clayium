package com.github.trcdevelopers.clayium.common.loaders.recipe

import com.github.trcdevelopers.clayium.common.clayenergy.ClayEnergy
import com.github.trcdevelopers.clayium.common.items.metaitem.MetaItemClayParts
import com.github.trcdevelopers.clayium.common.recipe.registry.CRecipes
import com.github.trcdevelopers.clayium.common.unification.material.CMaterials
import com.github.trcdevelopers.clayium.common.unification.ore.OrePrefix

object InscriberRecipeLoader {
    fun registerRecipes() {
        val registry = CRecipes.INSCRIBER

        registry.builder()
            .input(MetaItemClayParts.CEE_BOARD)
            .input(MetaItemClayParts.EnergeticClayDust, 32)
            .output(MetaItemClayParts.CEECircuit)
            .tier(0).CEt(ClayEnergy.micro(20)).duration(20)
            .buildAndRegister()

        registry.builder()
            .input(MetaItemClayParts.CLAY_CIRCUIT_BOARD)
            .input(OrePrefix.dust, CMaterials.denseClay, 6)
            .output(MetaItemClayParts.CLAY_CIRCUIT)
            .tier(0).CEt(ClayEnergy.micro(20)).duration(20)
            .buildAndRegister()

        registry.builder()
            .input(MetaItemClayParts.CLAY_CIRCUIT_BOARD)
            .input(MetaItemClayParts.EnergeticClayDust, 32)
            .output(MetaItemClayParts.BASIC_CIRCUIT)
            .tier(0).CEt(ClayEnergy.micro(20)).duration(20)
            .buildAndRegister()

        registry.builder()
            .input(OrePrefix.plate, CMaterials.impureSilicon)
            .input(MetaItemClayParts.EnergeticClayDust, 32)
            .output(MetaItemClayParts.ADVANCED_CIRCUIT)
            .tier(0).CEt(ClayEnergy.milli(1)).duration(120)
            .buildAndRegister()

        registry.builder()
            .input(OrePrefix.plate, CMaterials.silicon)
            .input(MetaItemClayParts.EnergeticClayDust, 32)
            .output(MetaItemClayParts.PRECISION_CIRCUIT)
            .tier(0).CEt(ClayEnergy.milli(10)).duration(120)
            .buildAndRegister()

        registry.builder()
            .input(MetaItemClayParts.PRECISION_CIRCUIT)
            .input(MetaItemClayParts.EnergeticClayDust, 32)
            .output(MetaItemClayParts.INTEGRATED_CIRCUIT)
            .tier(0).CEt(ClayEnergy.milli(100)).duration(1200)
            .buildAndRegister()
    }
}