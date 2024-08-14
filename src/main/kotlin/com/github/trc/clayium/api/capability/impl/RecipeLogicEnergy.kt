package com.github.trc.clayium.api.capability.impl

import com.github.trc.clayium.api.ClayEnergy
import com.github.trc.clayium.api.metatileentity.MetaTileEntity
import com.github.trc.clayium.common.recipe.Recipe
import com.github.trc.clayium.common.recipe.registry.RecipeRegistry

open class RecipeLogicEnergy(
    metaTileEntity: MetaTileEntity,
    recipeRegistry: RecipeRegistry<*>,
    private val energyHolder: ClayEnergyHolder,
) : AbstractRecipeLogic(metaTileEntity, recipeRegistry) {
    private var durationMultiplier = 1.0
    private var energyConsumingMultiplier = 1.0

    override fun drawEnergy(ce: ClayEnergy, simulate: Boolean): Boolean {
        val energy = ce.energy * energyConsumingMultiplier
        return energyHolder.drawEnergy(ClayEnergy(energy.toLong()), simulate)
    }

    override fun addProgress() {
        currentProgress += metaTileEntity.overclock.toLong()
    }

    override fun prepareRecipe(recipe: Recipe) {
        super.prepareRecipe(recipe)
        this.requiredProgress = (recipe.duration * durationMultiplier).toLong()
    }

    fun setDurationMultiplier(provider: (tier: Int) -> Double): RecipeLogicEnergy {
        durationMultiplier = provider(this.tierNum)
        return this
    }

    fun setEnergyConsumingMultiplier(provider: (tier: Int) -> Double): RecipeLogicEnergy {
        energyConsumingMultiplier = provider(this.tierNum)
        return this
    }
}