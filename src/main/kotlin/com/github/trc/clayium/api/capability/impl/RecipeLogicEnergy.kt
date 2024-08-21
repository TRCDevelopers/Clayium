package com.github.trc.clayium.api.capability.impl

import com.github.trc.clayium.api.ClayEnergy
import com.github.trc.clayium.api.metatileentity.MetaTileEntity
import com.github.trc.clayium.common.recipe.registry.RecipeRegistry

open class RecipeLogicEnergy(
    metaTileEntity: MetaTileEntity,
    recipeRegistry: RecipeRegistry<*>,
    private val energyHolder: ClayEnergyHolder,
) : AbstractRecipeLogic(metaTileEntity, recipeRegistry) {
    private var durationMultiplier = 1.0
    private var energyConsumingMultiplier = 1.0

    override fun drawEnergy(ce: ClayEnergy, simulate: Boolean): Boolean {
        return energyHolder.drawEnergy(ClayEnergy((ce.energy * ocHandler.accelerationFactor).toLong()), simulate)
    }

    override fun applyOverclock(cePt: ClayEnergy, duration: Long, compensatedFactor: Double): LongArray {
        val (cet, duration) = super.applyOverclock(cePt, duration, compensatedFactor)
        return longArrayOf((cet * energyConsumingMultiplier).toLong(), (duration * durationMultiplier).toLong())
    }

    /**
     * Mainly used by Condensers, Grinders, Centrifuges.
     * The speed of these machines depends on the tier.
     */
    fun setDurationMultiplier(provider: (tier: Int) -> Double): RecipeLogicEnergy {
        durationMultiplier = provider(this.getTier())
        return this
    }

    /**
     * Mainly used by Condensers, Grinders, Centrifuges.
     * The speed of these machines depends on the tier.
     */
    fun setEnergyConsumingMultiplier(provider: (tier: Int) -> Double): RecipeLogicEnergy {
        energyConsumingMultiplier = provider(this.getTier())
        return this
    }
}