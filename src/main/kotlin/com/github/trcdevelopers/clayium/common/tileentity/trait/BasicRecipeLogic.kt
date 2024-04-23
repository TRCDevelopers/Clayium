package com.github.trcdevelopers.clayium.common.tileentity.trait

import com.cleanroommc.modularui.api.widget.IWidget
import com.github.trcdevelopers.clayium.common.recipe.registry.RecipeRegistry
import com.github.trcdevelopers.clayium.common.tileentity.TileEntityMachine

class BasicRecipeLogic(
    tileEntityMachine: TileEntityMachine,
    tier: Int,
    private val recipeRegistry: RecipeRegistry<*>,
) : TETrait(tileEntityMachine, tier) {

    private var requiredProgress = 0
    private var currentProgress = 0

    private var invalidInputsForRecipes = false
    private var outputsFull = false

    override fun update() {
        if (tileEntity.world?.isRemote == true) return
        if (currentProgress != 0) {
            updateRecipeProgress()
        }
        if (currentProgress == 0 && shouldSearchNewRecipe()) {
            trySearchNewRecipe()
        }
    }

    private fun updateRecipeProgress() {
    }

    private fun shouldSearchNewRecipe(): Boolean {
        return canWorkWithInputs() && canFitNewOutputs()
    }

    private fun canWorkWithInputs(): Boolean {
        if (invalidInputsForRecipes && !tileEntity.hasNotifiedInputs) return false

        invalidInputsForRecipes = false
        tileEntity.hasNotifiedInputs = false
        return true
    }

    private fun canFitNewOutputs(): Boolean {
        if (outputsFull && !tileEntity.hasNotifiedOutputs) return false

        outputsFull = false
        tileEntity.hasNotifiedOutputs = false
        return true
    }

    private fun trySearchNewRecipe() {
    }

    fun getProgressBar(): IWidget {
        return TODO()
    }
}