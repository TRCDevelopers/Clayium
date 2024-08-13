package com.github.trc.clayium.common.loaders.recipe

import com.github.trc.clayium.api.ClayEnergy
import com.github.trc.clayium.common.items.metaitem.MetaItemClayParts
import com.github.trc.clayium.common.recipe.registry.CRecipes
import com.github.trc.clayium.common.unification.material.CMaterials
import com.github.trc.clayium.common.unification.ore.OrePrefix

object InscriberRecipeLoader {
    fun registerRecipes() {
        val registry = CRecipes.INSCRIBER

        registry.builder()
            .input(MetaItemClayParts.CeeBoard)
            .input(MetaItemClayParts.EnergizedClayDust, 32)
            .output(MetaItemClayParts.CeeCircuit)
            .tier(0).CEt(ClayEnergy.micro(20)).duration(20)
            .buildAndRegister()

        registry.builder()
            .input(MetaItemClayParts.ClayCircuitBoard)
            .input(OrePrefix.dust, CMaterials.denseClay, 6)
            .output(MetaItemClayParts.ClayCircuit)
            .tier(0).CEt(ClayEnergy.micro(20)).duration(20)
            .buildAndRegister()

        registry.builder()
            .input(MetaItemClayParts.ClayCircuitBoard)
            .input(MetaItemClayParts.EnergizedClayDust, 32)
            .output(MetaItemClayParts.BasicCircuit)
            .tier(0).CEt(ClayEnergy.micro(20)).duration(20)
            .buildAndRegister()

        registry.builder()
            .input(OrePrefix.plate, CMaterials.impureSilicon)
            .input(MetaItemClayParts.EnergizedClayDust, 32)
            .output(MetaItemClayParts.AdvancedCircuit)
            .tier(0).CEt(ClayEnergy.milli(1)).duration(120)
            .buildAndRegister()

        registry.builder()
            .input(OrePrefix.plate, CMaterials.silicon)
            .input(MetaItemClayParts.EnergizedClayDust, 32)
            .output(MetaItemClayParts.PrecisionCircuit)
            .tier(0).CEt(ClayEnergy.milli(10)).duration(120)
            .buildAndRegister()
    }
}