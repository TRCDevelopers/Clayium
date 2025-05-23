package com.github.trc.clayium.common.recipe.registry

import com.github.trc.clayium.api.MOD_ID
import com.github.trc.clayium.api.recipe.IRecipeProvider
import com.github.trc.clayium.api.unification.stack.ItemAndMeta
import com.github.trc.clayium.api.util.CLog
import com.github.trc.clayium.api.util.Mods
import com.github.trc.clayium.common.recipe.Recipe
import com.github.trc.clayium.common.recipe.RecipeCategory
import com.github.trc.clayium.common.recipe.builder.RecipeBuilder
import com.github.trc.clayium.integration.groovy.GroovyScriptModule
import com.github.trc.clayium.integration.groovy.RecipeRegistryGrsAdapter
import net.minecraft.item.ItemStack

open class RecipeRegistry<R: RecipeBuilder<R>>(
    /** Used for getting faceTexture location in MTE, and used as UID in JEI. */
    val category: RecipeCategory,
    private val builderSample: R,
    /**
     * Max input slot count. Used in MTEs to determine input slot count.
     */
    val maxInputs: Int,
    /**
     * Max output slot count.  Used in MTEs to determine output slot count.
     */
    val maxOutputs: Int,
) : IRecipeProvider {

    constructor(translationKey: String, builderSample: R, maxInputs: Int, maxOutputs: Int) :
            this(RecipeCategory.create(MOD_ID, translationKey), builderSample, maxInputs, maxOutputs)

    val categoryName = category.categoryName
    override val jeiCategory = category.uniqueId

    val grsVirtualizedRegistry: RecipeRegistryGrsAdapter?
        = if (Mods.GroovyScript.isModLoaded) RecipeRegistryGrsAdapter(this) else null

    init {
        builderSample.setRegistry(this)
    }

    /**
     * Always sorted.
     */
    private val _recipesListForJei = mutableListOf<Recipe>()

    /**
     * Value List MUST always be sorted with `Recipe.tier` then `Recipe.priority` in descending order, i.e. higher is preferred.
     */
    private val recipeSearchMap = mutableMapOf<ItemAndMeta, MutableList<Recipe>>()

    fun builder(): R {
        return builderSample.copy()
    }

    fun register(provider: R.() -> Unit) {
        val builder = builder()
        provider(builder)
        builder.buildAndRegister()
    }

    fun findRecipe(machineTier: Int, inputsIn: List<ItemStack>): Recipe? {
        inputsIn.forEach {
            if (it.isEmpty) return@forEach
            val recipes = recipeSearchMap[ItemAndMeta(it)]
            if (recipes == null) return@forEach
            for (recipe in recipes) {
                if (recipe.matches(inputsIn, machineTier)) return recipe
            }
        }
        return null
    }

    override fun searchRecipe(machineTier: Int, inputs: List<ItemStack>): Recipe? {
        return findRecipe(machineTier, inputs)
    }

    fun addRecipe(recipe: Recipe) {
        validateRecipe(recipe)
            .onSuccess { recipe ->
                _recipesListForJei.add(recipe)
                _recipesListForJei.sortWith(TIER_DURATION_CE_REVERSED)

                addRecipeValidated(recipe)

                if (GroovyScriptModule.isCurrentlyRunning()) {
                    grsVirtualizedRegistry?.addScripted(recipe)
                }
            }
            .onFailure { CLog.error("Failed to add recipe: $recipe") }
    }

    private fun addRecipeValidated(recipe: Recipe) {
        val inputStacks = recipe.inputs.map { it.stacks }.flatten()

        for (stack in inputStacks) {
            val key = ItemAndMeta(stack)
            var recipes = recipeSearchMap[key]
            if (recipes == null) {
                recipes = mutableListOf<Recipe>()
                recipes.add(recipe)
                recipeSearchMap[key] = recipes
            } else {
                recipes.add(recipe)
                recipes.sortWith(PRIO_THEN_TIER_DESCENDING)
            }
        }
        CLog.debug("Recipe added: {}", recipe)
    }

    fun removeRecipe(recipe: Recipe): Boolean {
        if (GroovyScriptModule.isCurrentlyRunning()) {
            grsVirtualizedRegistry?.addBackup(recipe)
        }
        _recipesListForJei.remove(recipe)
        var removed = false
        for (list in recipeSearchMap.values) {
            removed = removed || list.remove(recipe)
        }
        return removed
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
        return _recipesListForJei.sortedWith(TIER_DURATION_CE)
    }

    companion object {
        /**
         * in JEI, should be sorted with this.
         */
        val TIER_DURATION_CE = Comparator.comparingInt(Recipe::recipeTier)
            .thenComparingLong(Recipe::duration)
            .thenComparingLong { recipe -> recipe.cePerTick.energy }
        val TIER_DURATION_CE_REVERSED = TIER_DURATION_CE.reversed()

        /**
         * Recipe selection order.
         * for recipe map value list.
         */
        val PRIO_THEN_TIER_DESCENDING: Comparator<Recipe> = Comparator.comparingInt(Recipe::priority)
            .thenComparing(Recipe::recipeTier)
            .reversed()
    }
}