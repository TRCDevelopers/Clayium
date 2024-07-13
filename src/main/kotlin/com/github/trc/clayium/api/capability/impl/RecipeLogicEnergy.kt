package com.github.trc.clayium.api.capability.impl

import com.github.trc.clayium.api.metatileentity.MetaTileEntity
import com.github.trc.clayium.common.clayenergy.ClayEnergy
import com.github.trc.clayium.common.recipe.registry.RecipeRegistry

open class RecipeLogicEnergy(
    metaTileEntity: MetaTileEntity,
    recipeRegistry: RecipeRegistry<*>,
    private val energyHolder: ClayEnergyHolder,
) : AbstractRecipeLogic(metaTileEntity, recipeRegistry) {
    override fun drawEnergy(ce: ClayEnergy, simulate: Boolean): Boolean {
        return energyHolder.drawEnergy(ce, simulate)
    }
}