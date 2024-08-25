package com.github.trc.clayium.api.capability.impl

import com.github.trc.clayium.api.metatileentity.MetaTileEntity
import com.github.trc.clayium.common.recipe.registry.RecipeRegistry
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

    /**
     * Required Antimatter amounts:
     * 1->2: 1
     * 2->3: 2
     * 3->4: 2
     * 4->5: 3
     * 5->6: 4
     * 6->7: 5
     * 7->8: 8
     * 8->9: 10
     * 9->10: 13
     * 10->11: 17
     * 11->12: 23
     * 12->13: 30
     */

    companion object {
        private val ANTIMATTER_AMOUNTS = intArrayOf(1, 2, 2, 3, 4, 5, 8, 10, 13, 17, 23, 30)
    }
}