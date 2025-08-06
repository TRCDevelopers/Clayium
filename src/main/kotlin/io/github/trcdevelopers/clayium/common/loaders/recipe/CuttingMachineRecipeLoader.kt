package io.github.trcdevelopers.clayium.common.loaders.recipe

import io.github.trcdevelopers.clayium.api.ClayEnergy
import io.github.trcdevelopers.clayium.api.unification.material.CMaterials
import io.github.trcdevelopers.clayium.api.unification.ore.OrePrefix
import io.github.trcdevelopers.clayium.common.items.metaitem.MetaItemClayParts.LargeClayBall
import io.github.trcdevelopers.clayium.common.recipe.registry.CRecipes

object CuttingMachineRecipeLoader {
    fun registerRecipes() {
        val registry = CRecipes.CUTTING_MACHINE

        registry.builder()
            .input(LargeClayBall)
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