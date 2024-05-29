package com.github.trcdevelopers.clayium.api.capability.impl

import com.github.trcdevelopers.clayium.api.metatileentity.multiblock.MultiblockControllerBase
import com.github.trcdevelopers.clayium.common.clayenergy.ClayEnergy
import com.github.trcdevelopers.clayium.common.recipe.registry.RecipeRegistry

open class MultiblockRecipeLogic(
    private val controller: MultiblockControllerBase,
    recipeRegistry: RecipeRegistry<*>,
) : RecipeLogicEnergy(controller, recipeRegistry, controller.clayEnergyHolder) {
    override fun drawEnergy(ce: ClayEnergy, simulate: Boolean): Boolean {
        if (!controller.structureFormed) return false
        return super.drawEnergy(ce, simulate)
    }
}