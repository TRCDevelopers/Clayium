package com.github.trcdevelopers.clayium.common.recipe.loader

import com.github.trcdevelopers.clayium.common.clayenergy.ClayEnergy
import com.github.trcdevelopers.clayium.common.recipe.registry.CRecipes
import com.github.trcdevelopers.clayium.common.unification.material.CMaterials
import com.github.trcdevelopers.clayium.common.unification.ore.OrePrefix

object ChemicalReactorRecipeLoader {
    fun registerRecipes() {
        val registry = CRecipes.CHEMICAL_REACTOR
        registry.builder()
            .input(OrePrefix.dust, CMaterials.clay)
            .input(OrePrefix.dust, CMaterials.sodiumCarbonate)
            .output(OrePrefix.dust, CMaterials.quartz)
            .cePerTick(ClayEnergy.milli(1)).duration(120).tier(0)
            .buildAndRegister()

        registry.builder()
            .input(OrePrefix.gem, CMaterials.coal)
            .input(OrePrefix.dust, CMaterials.quartz)
            .output(OrePrefix.ingot, CMaterials.impureSilicon)
            .cePerTick(ClayEnergy.milli(1)).duration(120).tier(0)
            .buildAndRegister()

        registry.builder()
            .input(OrePrefix.gem, CMaterials.charcoal)
            .input(OrePrefix.dust, CMaterials.quartz)
            .output(OrePrefix.ingot, CMaterials.impureSilicon)
            .cePerTick(ClayEnergy.milli(1)).duration(120).tier(0)
            .buildAndRegister()

        registry.builder()
            .input(OrePrefix.dust, CMaterials.salt, 2)
            .input(OrePrefix.dust, CMaterials.calcareousClay)
            .output(OrePrefix.dust, CMaterials.calciumChloride)
            .output(OrePrefix.dust, CMaterials.sodiumCarbonate)
            .cePerTick(ClayEnergy.milli(10)).duration(120).tier(0)
            .buildAndRegister()

        // energized clay dust -> impure red/glowstone

        registry.builder()
            .input(OrePrefix.dust, CMaterials.denseClay)
            .output(OrePrefix.dust, CMaterials.impureSilicon)
            .output(OrePrefix.dust, CMaterials.aluminum)
            .cePerTick(ClayEnergy.milli(10)).duration(30).tier(5)

        registry.builder()
            .input(OrePrefix.dust, CMaterials.salt)
            .input(OrePrefix.dust, CMaterials.industrialClay)
            .output(OrePrefix.dust, CMaterials.quartz)
            .output(OrePrefix.dust, CMaterials.calciumChloride)
            .cePerTick(ClayEnergy.of(100)).duration(1).tier(8)
            .buildAndRegister()

        registry.builder()
            .input(OrePrefix.dust, CMaterials.quartz)
            .input(OrePrefix.dust, CMaterials.industrialClay)
            .output(OrePrefix.ingot, CMaterials.impureSilicon)
            .cePerTick(ClayEnergy.of(100)).duration(1).tier(8)
            .buildAndRegister()
    }
}