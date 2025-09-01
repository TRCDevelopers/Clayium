package io.github.trcdevelopers.clayium.api.recipe

import io.github.trcdevelopers.clayium.api.metatileentity.interfaces.IHasNotifiableInventory
import io.github.trcdevelopers.clayium.api.util.toList
import net.minecraftforge.items.IItemHandler

open class RecipeProvider(
    private val notifiableInventory: IHasNotifiableInventory,
    val inputInventory: IItemHandler,
    val outputInventory: IItemHandler,
    val recipeProvider: IRecipeRegistry,
) {
    private var inputsWereValid = true

    fun searchNewRecipe(machineTier: Int): IClayiumRecipe? {
        val shouldSearchForNewRecipe = this.canWorkWithInputs()
        if (!shouldSearchForNewRecipe) return null

        val inputsIn = inputInventory.toList()

        val newRecipe = recipeProvider.searchRecipe(machineTier, inputsIn)
        return newRecipe?.takeIf { it.outputs.canFit(outputInventory) }
    }

    protected fun canWorkWithInputs(): Boolean {
        if (!inputsWereValid && !notifiableInventory.hasNotifiedInputs) return false

        inputsWereValid = true
        notifiableInventory.hasNotifiedInputs = false
        return true
    }
}