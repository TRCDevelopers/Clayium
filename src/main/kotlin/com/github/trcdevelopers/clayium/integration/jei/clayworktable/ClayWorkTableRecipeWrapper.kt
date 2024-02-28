package com.github.trcdevelopers.clayium.integration.jei.clayworktable

import com.github.trcdevelopers.clayium.common.recipe.ClayWorkTableRecipe
import mezz.jei.api.ingredients.IIngredients
import mezz.jei.api.ingredients.VanillaTypes
import mezz.jei.api.recipe.IRecipeWrapper

class ClayWorkTableRecipeWrapper(
    val recipe: ClayWorkTableRecipe
) : IRecipeWrapper {
    override fun getIngredients(ingredients: IIngredients) {
        ingredients.setInputLists(VanillaTypes.ITEM, listOf(recipe.input.inputStacks))
        ingredients.setOutputs(VanillaTypes.ITEM, recipe.outputs)
    }
}