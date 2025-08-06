package io.github.trcdevelopers.clayium.common.loaders.recipe

import io.github.trcdevelopers.clayium.api.ClayEnergy
import io.github.trcdevelopers.clayium.api.unification.material.CMaterials
import io.github.trcdevelopers.clayium.api.unification.ore.OrePrefix
import io.github.trcdevelopers.clayium.common.recipe.registry.CRecipes
import net.minecraft.init.Items

object WireDrawingRecipeLoader {
    fun registerRecipes() {
        val registry = CRecipes.WIRE_DRAWING_MACHINE

        registry.builder()
            .input(Items.CLAY_BALL)
            .output(OrePrefix.stick, CMaterials.clay)
            .tier(0).CEt(ClayEnergy.micro(10)).duration(1)
            .buildAndRegister()

        for ((i, material) in listOf(CMaterials.clay, CMaterials.denseClay).withIndex()) {
            registry.builder()
                .input(OrePrefix.smallDisc, material)
                .output(OrePrefix.stick, material)
                .tier(0).CEt(ClayEnergy.micro(10)).duration(1 * (i + 1))
                .buildAndRegister()

            registry.builder()
                .input(OrePrefix.pipe, material)
                .output(OrePrefix.stick, material, 4)
                .tier(0).CEt(ClayEnergy.micro(10)).duration(1 * (i + 1))
                .buildAndRegister()

            registry.builder()
                .input(OrePrefix.cylinder, material)
                .output(OrePrefix.stick, material, 8)
                .tier(0).CEt(ClayEnergy.micro(10)).duration(3 * (i + 1))
                .buildAndRegister()
        }
    }
}