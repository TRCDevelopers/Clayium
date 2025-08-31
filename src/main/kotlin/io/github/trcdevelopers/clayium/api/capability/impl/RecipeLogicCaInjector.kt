package io.github.trcdevelopers.clayium.api.capability.impl

import io.github.trcdevelopers.clayium.api.metatileentity.MetaTileEntity
import io.github.trcdevelopers.clayium.common.recipe.registry.RecipeRegistry
import kotlin.math.pow

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

    override fun getProgressPerTick(): Long {
        return resonanceManager.resonance.pow(accelerationExponent).toLong()
    }
}