package com.github.trc.clayium.api.capability.impl

import com.github.trc.clayium.api.metatileentity.MetaTileEntity
import com.github.trc.clayium.common.recipe.registry.RecipeRegistry

class RecipeLogicCaInjector(
    metaTileEntity: MetaTileEntity,
    recipeRegistry: RecipeRegistry<*>,
    energyHolder: ClayEnergyHolder,
    private val resonanceManager: ResonanceManager,
) : RecipeLogicEnergy(metaTileEntity, recipeRegistry, energyHolder) {

    val accelerationExponent = when (metaTileEntity.tier.numeric) {
        9 -> 0.2
        10 -> 0.9
        11 -> 3.0
        else -> 1.0
    }

    override fun updateRecipeProgress() {
        if (drawEnergy(recipeCEt)) {
            currentProgress += Math.pow(resonanceManager.resonance, accelerationExponent).toLong()
        }
        if (currentProgress > requiredProgress) {
            completeRecipe()
        }
    }
}