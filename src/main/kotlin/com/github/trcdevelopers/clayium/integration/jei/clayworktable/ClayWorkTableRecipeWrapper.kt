package com.github.trcdevelopers.clayium.integration.jei.clayworktable

import com.github.trcdevelopers.clayium.common.recipe.clayworktable.ClayWorkTableRecipe
import mezz.jei.api.ingredients.IIngredients
import mezz.jei.api.ingredients.VanillaTypes
import mezz.jei.api.recipe.IRecipeWrapper

class ClayWorkTableRecipeWrapper(
    val recipe: ClayWorkTableRecipe
) : IRecipeWrapper {
    override fun getIngredients(ingredients: IIngredients) {
        ingredients.setInput(VanillaTypes.ITEM, recipe.input)
        ingredients.setOutputs(VanillaTypes.ITEM, listOf(recipe.primaryOutput, recipe.secondaryOutput))
    }
}