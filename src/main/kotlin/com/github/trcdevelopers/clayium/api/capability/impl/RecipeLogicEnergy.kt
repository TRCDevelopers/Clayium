package com.github.trcdevelopers.clayium.api.capability.impl

import com.github.trcdevelopers.clayium.api.metatileentity.MetaTileEntity
import com.github.trcdevelopers.clayium.common.clayenergy.ClayEnergy
import com.github.trcdevelopers.clayium.common.recipe.registry.RecipeRegistry

open class RecipeLogicEnergy(
    metaTileEntity: MetaTileEntity,
    recipeRegistry: RecipeRegistry<*>,
    private val energyHolder: ClayEnergyHolder,
) : AbstractRecipeLogic(metaTileEntity, recipeRegistry) {
    override fun drawEnergy(ce: ClayEnergy, simulate: Boolean): Boolean {
        return energyHolder.drawEnergy(ce, simulate)
    }
}