package io.github.trcdevelopers.clayium.common.loaders.recipe

import io.github.trcdevelopers.clayium.api.ClayEnergy
import io.github.trcdevelopers.clayium.api.unification.material.CMaterials
import io.github.trcdevelopers.clayium.api.unification.ore.OrePrefix
import io.github.trcdevelopers.clayium.common.items.metaitem.MetaItemClayParts
import io.github.trcdevelopers.clayium.common.recipe.registry.CRecipes

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