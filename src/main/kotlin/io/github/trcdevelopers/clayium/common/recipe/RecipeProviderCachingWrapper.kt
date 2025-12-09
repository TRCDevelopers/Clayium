package io.github.trcdevelopers.clayium.common.recipe

import io.github.trcdevelopers.clayium.api.metatileentity.MetaTileEntity
import io.github.trcdevelopers.clayium.api.recipe.IRecipeProvider

open class RecipeProviderCachingWrapper(
    private val recipeProvider: IRecipeProvider,
    private val metaTileEntity: MetaTileEntity,
) {
    private val lastRecipe: Recipe? = null

    val jeiCategories get() = recipeProvider.jeiCategories

    private var invalidInputsForRecipes = false

    protected open fun shouldSearchForRecipe(): Boolean {
        return canWorkWithInputs() && canFitNewOutputs()
    }

    protected fun canWorkWithInputs(): Boolean {
        if (invalidInputsForRecipes && !metaTileEntity.hasNotifiedInputs) return false

        invalidInputsForRecipes = false
        metaTileEntity.hasNotifiedInputs = false
        return true
    }

    protected fun canFitNewOutputs(): Boolean {
        return true

        // currently, NotifiableItemStackHandler.onContentsChanged isn't called
        // if the item is extracted without pressing a shift key in GUI.
        // therefore, metaTileEntity.hasNotifiedOutputs is remains false in that case.
        // so output full check is disabled.

//        if (outputsFull && !metaTileEntity.hasNotifiedOutputs) return false
//
//        outputsFull = false
//        metaTileEntity.hasNotifiedOutputs = false
//        return true
    }
}