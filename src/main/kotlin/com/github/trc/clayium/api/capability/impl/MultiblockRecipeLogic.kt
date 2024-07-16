package com.github.trc.clayium.api.capability.impl

import com.github.trc.clayium.api.metatileentity.multiblock.MultiblockControllerBase
import com.github.trc.clayium.common.clayenergy.ClayEnergy
import com.github.trc.clayium.common.recipe.registry.RecipeRegistry

open class MultiblockRecipeLogic(
    private val controller: MultiblockControllerBase,
    recipeRegistry: RecipeRegistry<*>,
) : RecipeLogicEnergy(controller, recipeRegistry, controller.clayEnergyHolder) {
    override fun drawEnergy(ce: ClayEnergy, simulate: Boolean): Boolean {
        if (!controller.structureFormed) return false
        return super.drawEnergy(ce, simulate)
    }
}