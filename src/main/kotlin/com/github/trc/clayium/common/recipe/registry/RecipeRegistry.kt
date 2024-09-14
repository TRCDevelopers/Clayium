package com.github.trc.clayium.common.recipe.registry

import com.github.trc.clayium.api.CValues
import com.github.trc.clayium.api.util.CLog
import com.github.trc.clayium.api.util.Mods
import com.github.trc.clayium.common.recipe.Recipe
import com.github.trc.clayium.common.recipe.RecipeCategory
import com.github.trc.clayium.common.recipe.builder.RecipeBuilder
import com.github.trc.clayium.integration.groovy.GroovyScriptModule
import com.github.trc.clayium.integration.groovy.RecipeRegistryGrsAdapter
import net.minecraft.item.ItemStack

open class RecipeRegistry<R: RecipeBuilder<R>>(
    val category: RecipeCategory,
    private val builderSample: R,
    val maxInputs: Int,
    val maxOutputs: Int,
) {

    constructor(translationKey: String, builderSample: R, maxInputs: Int, maxOutputs: Int) :
            this(RecipeCategory.create(CValues.MOD_ID, translationKey), builderSample, maxInputs, maxOutputs)

    val categoryName = category.categoryName

    val grsVirtualizedRegistry: RecipeRegistryGrsAdapter?
        = if (Mods.GroovyScript.isModLoaded) RecipeRegistryGrsAdapter(this) else null

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
            .onFailure { CLog.error("Failed to add recipe: $recipe") }
        if (GroovyScriptModule.isCurrentlyRunning()) {
            grsVirtualizedRegistry?.addScripted(recipe)
        }
    }

    fun removeRecipe(recipe: Recipe): Boolean {
        if (GroovyScriptModule.isCurrentlyRunning()) {
            grsVirtualizedRegistry?.addBackup(recipe)
        }
        return _recipes.remove(recipe)
    }

    private fun validateRecipe(recipe: Recipe): Result<Recipe> {
        if (!recipe.inputs.all { it.isValid() }) {
            CLog.error("invalid recipe: Input is invalid.")
            return Result.failure(IllegalArgumentException())
        }
        if (recipe.inputs.isEmpty()) {
            CLog.error("invalid recipe: Input is empty.")
            return Result.failure(IllegalArgumentException())
        }
        if (recipe.inputs.any { input -> input.stacks.any { stack -> stack.isEmpty } }) {
            CLog.error("invalid recipe: Input has an empty ItemStack.")
            return Result.failure(IllegalArgumentException())
        }
        if (recipe.outputs.any { it.isEmpty }) {
            CLog.error("invalid recipe: Output has an empty ItemStack.")
            return Result.failure(IllegalArgumentException())
        }
        if (recipe.recipeTier < 0) {
            CLog.info("invalid recipe: Tier is less than 0.")
            return Result.failure(IllegalArgumentException())
        }
        if (recipe.duration <= 0) {
            CLog.info("invalid recipe: Duration is less than or equal to 0.")
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