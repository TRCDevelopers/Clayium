package com.github.trc.clayium.common.loaders.recipe

import com.github.trc.clayium.common.clayenergy.ClayEnergy
import com.github.trc.clayium.common.items.metaitem.MetaItemClayParts
import com.github.trc.clayium.common.recipe.registry.CRecipes
import com.github.trc.clayium.common.unification.material.CMaterials
import com.github.trc.clayium.common.unification.ore.OrePrefix

object InscriberRecipeLoader {
    fun registerRecipes() {
        val registry = CRecipes.INSCRIBER

        registry.builder()
            .input(MetaItemClayParts.CEE_BOARD)
            .input(MetaItemClayParts.EnergizedClayDust, 32)
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
            .input(MetaItemClayParts.EnergizedClayDust, 32)
            .output(MetaItemClayParts.BASIC_CIRCUIT)
            .tier(0).CEt(ClayEnergy.micro(20)).duration(20)
            .buildAndRegister()

        registry.builder()
            .input(OrePrefix.plate, CMaterials.impureSilicon)
            .input(MetaItemClayParts.EnergizedClayDust, 32)
            .output(MetaItemClayParts.ADVANCED_CIRCUIT)
            .tier(0).CEt(ClayEnergy.milli(1)).duration(120)
            .buildAndRegister()

        registry.builder()
            .input(OrePrefix.plate, CMaterials.silicon)
            .input(MetaItemClayParts.EnergizedClayDust, 32)
            .output(MetaItemClayParts.PRECISION_CIRCUIT)
            .tier(0).CEt(ClayEnergy.milli(10)).duration(120)
            .buildAndRegister()
    }
}