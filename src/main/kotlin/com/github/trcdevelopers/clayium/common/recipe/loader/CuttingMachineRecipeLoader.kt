package com.github.trcdevelopers.clayium.common.recipe.loader

import com.github.trcdevelopers.clayium.common.clayenergy.ClayEnergy
import com.github.trcdevelopers.clayium.common.items.metaitem.MetaItemClayParts.LARGE_CLAY_BALL
import com.github.trcdevelopers.clayium.common.recipe.registry.CRecipes
import com.github.trcdevelopers.clayium.common.unification.material.CMaterials
import com.github.trcdevelopers.clayium.common.unification.ore.OrePrefix

object CuttingMachineRecipeLoader {
    fun registerRecipes() {
        val registry = CRecipes.CUTTING_MACHINE

        registry.builder()
            .input(LARGE_CLAY_BALL)
            .output(OrePrefix.disc, CMaterials.clay)
            .tier(0).CEt(ClayEnergy.micro(10)).duration(2)
            .buildAndRegister()

        registry.builder()
            .input(OrePrefix.largePlate, CMaterials.clay)
            .output(OrePrefix.disc, CMaterials.clay, 2)
            .tier(0).CEt(ClayEnergy.micro(10)).duration(3)
            .buildAndRegister()

        registry.builder()
            .input(OrePrefix.cylinder, CMaterials.clay)
            .output(OrePrefix.smallDisc, CMaterials.clay, 8)
            .tier(0).CEt(ClayEnergy.micro(10)).duration(2)
            .buildAndRegister()

        registry.builder()
            .input(OrePrefix.plate, CMaterials.clay)
            .output(OrePrefix.smallDisc, CMaterials.clay, 4)
            .tier(0).CEt(ClayEnergy.micro(10)).duration(3)
            .buildAndRegister()

        registry.builder()
            .input(OrePrefix.stick, CMaterials.clay)
            .output(OrePrefix.shortStick, CMaterials.clay, 2)
            .tier(0).CEt(ClayEnergy.micro(10)).duration(1)
            .buildAndRegister()

        registry.builder()
            .input(OrePrefix.largePlate, CMaterials.denseClay)
            .output(OrePrefix.disc, CMaterials.denseClay, 2)
            .tier(0).CEt(ClayEnergy.micro(10)).duration(6)
            .buildAndRegister()

        registry.builder()
            .input(OrePrefix.cylinder, CMaterials.denseClay)
            .output(OrePrefix.smallDisc, CMaterials.denseClay, 8)
            .tier(0).CEt(ClayEnergy.micro(10)).duration(4)
            .buildAndRegister()

        registry.builder()
            .input(OrePrefix.plate, CMaterials.denseClay)
            .output(OrePrefix.smallDisc, CMaterials.denseClay, 4)
            .tier(0).CEt(ClayEnergy.micro(10)).duration(6)
            .buildAndRegister()

        registry.builder()
            .input(OrePrefix.stick, CMaterials.denseClay)
            .output(OrePrefix.shortStick, CMaterials.denseClay, 2)
            .tier(0).CEt(ClayEnergy.micro(10)).duration(2)
            .buildAndRegister()
    }
}