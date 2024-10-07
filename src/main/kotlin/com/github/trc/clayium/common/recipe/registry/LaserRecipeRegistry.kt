package com.github.trc.clayium.common.recipe.registry

import com.github.trc.clayium.api.util.CLog
import com.github.trc.clayium.api.util.Mods
import com.github.trc.clayium.common.recipe.LaserRecipe
import com.github.trc.clayium.common.recipe.builder.LaserRecipeBuilder
import com.github.trc.clayium.integration.groovy.GroovyScriptModule
import com.github.trc.clayium.integration.groovy.LaserRecipeRegistryGrsAdapter
import net.minecraft.block.state.IBlockState

private val RECIPE_COMPARATOR = compareBy<LaserRecipe> { it.energyMin }.reversed()

class LaserRecipeRegistry {
    private val _recipes = mutableListOf<LaserRecipe>()
    val recipes get() = _recipes.toList()

    fun register(recipe: LaserRecipe) {
        _recipes.add(recipe)
        _recipes.sortWith(RECIPE_COMPARATOR)
    }
    val grsVirtualizedRegistry: LaserRecipeRegistryGrsAdapter?
            = if (Mods.GroovyScript.isModLoaded) LaserRecipeRegistryGrsAdapter(this) else null

    fun register(create: LaserRecipeBuilder.() -> Unit) {
        val builder = LaserRecipeBuilder(this)
        builder.create()
        builder.buildAndRegister()
    }

    fun builder() = LaserRecipeBuilder(this)

    fun getRecipe(input: IBlockState, energy: Double): LaserRecipe? {
        for (recipe in _recipes) {
            if (recipe.matches(input, energy)) {
                return recipe
            }
        }
        return null
    }

    fun addRecipe(recipe: LaserRecipe) {
        validateRecipe(recipe)
            .onSuccess { recipe ->
                _recipes.add(recipe)
                _recipes.sortWith(RECIPE_COMPARATOR)
            }
            .onFailure { CLog.error("Failed to add recipe: $recipe") }
        if (GroovyScriptModule.isCurrentlyRunning()) {
            grsVirtualizedRegistry?.addScripted(recipe)
        }
    }

    fun removeRecipe(recipe: LaserRecipe): Boolean {
        if (GroovyScriptModule.isCurrentlyRunning()) {
            grsVirtualizedRegistry?.addBackup(recipe)
        }
        return _recipes.remove(recipe)
    }

    fun validateRecipe(recipe: LaserRecipe): Result<LaserRecipe> {
        if (recipe.energyMin < 0) {
            CLog.error("invalid recipe: energyMin should be greater than or equal to 0")
            return Result.failure(IllegalArgumentException())
        }
        if (recipe.requiredEnergy < 0) {
            CLog.error("invalid recipe: requiredEnergy should be greater than or equal to 0")
            return Result.failure(IllegalArgumentException())
        }
        if (recipe.energyMax < 0) {
            CLog.error("invalid recipe: energyMax should be greater than or equal to 0")
            return Result.failure(IllegalArgumentException())
        }
        return Result.success(recipe)
    }

    fun getAllRecipes(): List<LaserRecipe> {
        return _recipes.sortedWith(RECIPE_COMPARATOR)
    }
}
