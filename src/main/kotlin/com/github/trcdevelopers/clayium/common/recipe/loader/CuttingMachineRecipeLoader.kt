package com.github.trcdevelopers.clayium.common.recipe.loader

import com.github.trcdevelopers.clayium.common.clayenergy.ClayEnergy
import com.github.trcdevelopers.clayium.common.items.metaitem.MetaItemClayParts
import com.github.trcdevelopers.clayium.common.recipe.registry.CRecipes
import com.github.trcdevelopers.clayium.common.unification.material.CMaterials
import com.github.trcdevelopers.clayium.common.unification.ore.OrePrefix

object CuttingMachineRecipeLoader {
    fun registerRecipes() {
        val registry = CRecipes.CUTTING_MACHINE

        registry.builder()
            .input(MetaItemClayParts.LARGE_CLAY_BALL)
            .output(MetaItemClayParts.CLAY_DISC)
            .tier(0).CEt(ClayEnergy.micro(10)).duration(2)
            .buildAndRegister()

        registry.builder()
            .input(OrePrefix.largePlate, CMaterials.clay)
            .output(MetaItemClayParts.CLAY_DISC, 2)
            .tier(0).CEt(ClayEnergy.micro(10)).duration(3)
            .buildAndRegister()

        registry.builder()
            .input(MetaItemClayParts.CLAY_CYLINDER)
            .output(MetaItemClayParts.CLAY_SMALL_DISC, 8)
            .tier(0).CEt(ClayEnergy.micro(10)).duration(2)
            .buildAndRegister()

        registry.builder()
            .input(OrePrefix.plate, CMaterials.clay)
            .output(MetaItemClayParts.CLAY_SMALL_DISC, 4)
            .tier(0).CEt(ClayEnergy.micro(10)).duration(3)
            .buildAndRegister()

        registry.builder()
            .input(MetaItemClayParts.CLAY_STICK)
            .output(MetaItemClayParts.CLAY_SHORT_STICK, 2)
            .tier(0).CEt(ClayEnergy.micro(10)).duration(1)
            .buildAndRegister()

        registry.builder()
            .input(OrePrefix.largePlate, CMaterials.denseClay)
            .output(MetaItemClayParts.DENSE_CLAY_DISC, 2)
            .tier(0).CEt(ClayEnergy.micro(10)).duration(6)
            .buildAndRegister()

        registry.builder()
            .input(MetaItemClayParts.DENSE_CLAY_CYLINDER)
            .output(MetaItemClayParts.DENSE_CLAY_SMALL_DISC, 8)
            .tier(0).CEt(ClayEnergy.micro(10)).duration(4)
            .buildAndRegister()

        registry.builder()
            .input(OrePrefix.plate, CMaterials.denseClay)
            .output(MetaItemClayParts.DENSE_CLAY_SMALL_DISC, 4)
            .tier(0).CEt(ClayEnergy.micro(10)).duration(6)
            .buildAndRegister()

        registry.builder()
            .input(MetaItemClayParts.DENSE_CLAY_STICK)
            .output(MetaItemClayParts.DENSE_CLAY_SHORT_STICK, 2)
            .tier(0).CEt(ClayEnergy.micro(10)).duration(2)
            .buildAndRegister()
    }
}