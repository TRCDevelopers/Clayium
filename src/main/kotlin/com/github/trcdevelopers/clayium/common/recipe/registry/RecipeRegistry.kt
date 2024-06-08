package com.github.trcdevelopers.clayium.common.recipe.registry

import com.github.trcdevelopers.clayium.common.Clayium
import com.github.trcdevelopers.clayium.common.recipe.Recipe
import com.github.trcdevelopers.clayium.common.recipe.RecipeCategory
import com.github.trcdevelopers.clayium.common.recipe.builder.RecipeBuilder
import net.minecraft.item.ItemStack

class RecipeRegistry<R: RecipeBuilder<R>>(
    val category: RecipeCategory,
    private val builderSample: R,
    val maxInputs: Int,
    val maxOutputs: Int,
) {

    constructor(translationKey: String, builderSample: R, maxInputs: Int, maxOutputs: Int) : this(RecipeCategory.create(Clayium.MOD_ID, translationKey), builderSample, maxInputs, maxOutputs)

    init {
        builderSample.setRegistry(this)
    }

    private val _recipes = mutableListOf<Recipe>()
    val recipesList: List<Recipe> = _recipes

    fun builder(): R {
        return builderSample.copy()
    }

    fun register(provider: R.() -> Unit) {
        val builder = builder()
        provider(builder)
        builder.buildAndRegister()
    }

    fun findRecipe(tier: Int, inputsIn: List<ItemStack>): Recipe? {
        return _recipes.find {
            it.tierNumeric <= tier && it.matches(inputsIn)
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
        if (recipe.tierNumeric < 0) {
            Clayium.LOGGER.info("invalid recipe: Tier is less than 0.")
            return Result.failure(IllegalArgumentException())
        }
        if (recipe.duration <= 0) {
            Clayium.LOGGER.info("invalid recipe: Duration is less than or equal to 0.")
            return Result.failure(IllegalArgumentException())
        }
        return Result.success(recipe)
    }

    fun getAllRecipes(): List<Recipe> {
        return _recipes.toList()
    }
}