package com.github.trc.clayium.common.recipe.registry

import com.github.trc.clayium.api.CValues
import com.github.trc.clayium.common.Clayium
import com.github.trc.clayium.common.recipe.Recipe
import com.github.trc.clayium.common.recipe.RecipeCategory
import com.github.trc.clayium.common.recipe.builder.RecipeBuilder
import net.minecraft.item.ItemStack

open class RecipeRegistry<R: RecipeBuilder<R>>(
    val category: RecipeCategory,
    private val builderSample: R,
    val maxInputs: Int,
    val maxOutputs: Int,
) {

    constructor(translationKey: String, builderSample: R, maxInputs: Int, maxOutputs: Int) :
            this(RecipeCategory.create(CValues.MOD_ID, translationKey), builderSample, maxInputs, maxOutputs)

    val translationKey = category.translationKey

    init {
        builderSample.setRegistry(this)
    }

    private val _recipes = mutableListOf<Recipe>()

    fun builder(): R {
        return builderSample.copy()
    }

    fun register(provider: R.() -> Unit) {
        val builder = builder()
        provider(builder)
        builder.buildAndRegister()
    }

    //todo use hash table?
    fun findRecipe(machineTier: Int, inputsIn: List<ItemStack>): Recipe? {
        return _recipes.firstOrNull { it.matches(inputsIn, machineTier) }
    }

    fun addRecipe(recipe: Recipe) {
        validateRecipe(recipe)
            .onSuccess { recipe ->
                _recipes.add(recipe)
                _recipes.sortWith(TIER_DURATION_CE_REVERSED)
            }
            .onFailure { Clayium.LOGGER.error("Failed to add recipe: $recipe") }
    }

    private fun validateRecipe(recipe: Recipe): Result<Recipe> {
        if (recipe.inputs.isEmpty()) {
            Clayium.LOGGER.error("invalid recipe: Input is empty.")
            return Result.failure(IllegalArgumentException())
        }
        if (recipe.inputs.any { input -> input.stacks.any { stack -> stack.isEmpty } }) {
            Clayium.LOGGER.error("invalid recipe: Input has an empty ItemStack.")
            return Result.failure(IllegalArgumentException())
        }
        if (recipe.outputs.any { it.isEmpty }) {
            Clayium.LOGGER.error("invalid recipe: Output has an empty ItemStack.")
            return Result.failure(IllegalArgumentException())
        }
        if (recipe.recipeTier < 0) {
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
        return _recipes.sortedWith(TIER_DURATION_CE)
    }

    companion object {
        val TIER_DURATION_CE = Comparator.comparingInt(Recipe::recipeTier)
            .thenComparingLong(Recipe::duration)
            .thenComparingLong { recipe -> recipe.cePerTick.energy }

        val TIER_DURATION_CE_REVERSED = TIER_DURATION_CE.reversed()
    }
}