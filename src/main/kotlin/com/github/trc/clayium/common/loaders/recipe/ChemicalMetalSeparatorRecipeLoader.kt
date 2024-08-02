package com.github.trc.clayium.common.loaders.recipe

import com.github.trc.clayium.api.ClayEnergy
import com.github.trc.clayium.common.recipe.registry.CRecipes
import com.github.trc.clayium.common.unification.material.CMaterials
import com.github.trc.clayium.common.unification.ore.OrePrefix


object ChemicalMetalSeparatorRecipeLoader {
    fun registerRecipes() {
        val registry = CRecipes.CHEMICAL_METAL_SEPARATOR

        registry.builder()
            .input(OrePrefix.dust, CMaterials.industrialClay)
            .duration(40).CEt(ClayEnergy.milli(50))
            .chancedOutput(OrePrefix.impureDust, CMaterials.aluminum, 200)
            .chancedOutput(OrePrefix.impureDust, CMaterials.manganese, 80)
            .chancedOutput(OrePrefix.impureDust, CMaterials.magnesium, 60)
            .chancedOutput(OrePrefix.impureDust, CMaterials.sodium, 40)
            .chancedOutput(OrePrefix.impureDust, CMaterials.calcium, 20)
            .chancedOutput(OrePrefix.impureDust, CMaterials.potassium, 15)
            .chancedOutput(OrePrefix.impureDust, CMaterials.nickel, 13)
            .chancedOutput(OrePrefix.impureDust, CMaterials.zinc, 10)
            .chancedOutput(OrePrefix.impureDust, CMaterials.iron, 9)
            .chancedOutput(OrePrefix.impureDust, CMaterials.beryllium, 8)
            .chancedOutput(OrePrefix.impureDust, CMaterials.lithium, 7)
            .chancedOutput(OrePrefix.impureDust, CMaterials.lead, 6)
            .chancedOutput(OrePrefix.impureDust, CMaterials.zirconium, 5)
            .chancedOutput(OrePrefix.impureDust, CMaterials.hafnium, 4)
            .chancedOutput(OrePrefix.impureDust, CMaterials.chromium, 3)
            .chancedOutput(OrePrefix.impureDust, CMaterials.titanium, 3)
            .chancedOutput(OrePrefix.impureDust, CMaterials.strontium, 2)
            .chancedOutput(OrePrefix.impureDust, CMaterials.barium, 2)
            .chancedOutput(OrePrefix.impureDust, CMaterials.copper, 1)
            .buildAndRegister()
    }
}
