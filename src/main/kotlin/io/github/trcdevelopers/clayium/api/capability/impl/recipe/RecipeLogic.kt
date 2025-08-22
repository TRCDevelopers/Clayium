package io.github.trcdevelopers.clayium.api.capability.impl.recipe

import io.github.trcdevelopers.clayium.api.recipe.IRecipeProvider
import net.minecraftforge.items.IItemHandler

open class RecipeLogic(
    private val inputInventory: IItemHandler,
    private val outputInventory: IItemHandler,
    private val recipeProvider: IRecipeProvider,
) {

}