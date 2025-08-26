package io.github.trcdevelopers.clayium.api.recipe

import io.github.trcdevelopers.clayium.api.metatileentity.interfaces.IHasNotifiableInventory
import io.github.trcdevelopers.clayium.api.util.toList
import io.github.trcdevelopers.clayium.common.recipe.Recipe
import net.minecraftforge.items.IItemHandler

open class RecipeLogic(
    private val notifiableInventory: IHasNotifiableInventory,
    private val inputInventory: IItemHandler,
    private val outputInventory: IItemHandler,
    private val recipeProvider: IRecipeProvider,
) {
    private var inputsWereValid = true

    fun searchNewRecipe(machineTier: Int): Recipe? {
        val shouldSearchForNewRecipe = this.canWorkWithInputs()
        if (!shouldSearchForNewRecipe) return null

        val inputsIn = inputInventory.toList()

        return recipeProvider.searchRecipe(machineTier, inputsIn)
    }

    protected fun canWorkWithInputs(): Boolean {
        if (!inputsWereValid && !notifiableInventory.hasNotifiedInputs) return false

        inputsWereValid = true
        notifiableInventory.hasNotifiedInputs = false
        return true
    }
}