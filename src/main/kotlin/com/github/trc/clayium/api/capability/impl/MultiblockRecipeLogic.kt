package com.github.trc.clayium.api.capability.impl

import com.github.trc.clayium.api.ClayEnergy
import com.github.trc.clayium.api.metatileentity.WorkableMetaTileEntity
import com.github.trc.clayium.common.recipe.registry.RecipeRegistry

open class MultiblockRecipeLogic(
    metaTileEntity: WorkableMetaTileEntity,
    recipeRegistry: RecipeRegistry<*>,
    private val structureFormed: () -> Boolean,
) : RecipeLogicEnergy(metaTileEntity, recipeRegistry, metaTileEntity.clayEnergyHolder) {
    override fun drawEnergy(ce: ClayEnergy, simulate: Boolean): Boolean {
        if (!structureFormed()) return false
        return super.drawEnergy(ce, simulate)
    }
}