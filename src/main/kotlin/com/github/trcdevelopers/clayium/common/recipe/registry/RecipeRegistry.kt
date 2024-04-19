package com.github.trcdevelopers.clayium.common.recipe.registry

import com.github.trcdevelopers.clayium.common.Clayium
import com.github.trcdevelopers.clayium.common.recipe.Recipe
import com.github.trcdevelopers.clayium.common.recipe.RecipeCategory
import net.minecraft.item.ItemStack

class RecipeRegistry(
    val category: RecipeCategory,
    private val maxInputs: Int,
    private val maxOutputs: Int,
) {
    val _recipes = mutableListOf<Recipe>()

    fun findRecipe(tier: Int, inputsIn: List<ItemStack>): Recipe? {
        return _recipes.find {
            it.tier >= tier && it.matches(inputsIn)
        }
    }

    fun addRecipe(recipe: Recipe) {
        validateRecipe(recipe)
            .onSuccess { _recipes.add(it) }
            .onFailure { Clayium.LOGGER.error("Failed to add recipe: $recipe") }
    }

    private fun validateRecipe(recipe: Recipe): Result<Recipe> {
        if (recipe.inputs.isEmpty()) {
            Clayium.LOGGER.error("invalid recipe: Input is empty.")
            return Result.failure(IllegalArgumentException())
        }
        if (recipe.tier < 0) {
            Clayium.LOGGER.info("invalid recipe: Tier is less than 0.")
            return Result.failure(IllegalArgumentException())
        }
        if (recipe.duration <= 0) {
            Clayium.LOGGER.info("invalid recipe: Duration is less than or equal to 0.")
            return Result.failure(IllegalArgumentException())
        }
        return Result.success(recipe)
    }
}