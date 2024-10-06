package com.github.trc.clayium.common.recipe.registry

import com.github.trc.clayium.common.recipe.LaserRecipe
import com.github.trc.clayium.common.recipe.builder.LaserRecipeBuilder
import net.minecraft.block.state.IBlockState

private val RECIPE_COMPARATOR = compareBy<LaserRecipe> { it.energyMin }.reversed()

class LaserRecipeRegistry {
    private val _recipes = mutableListOf<LaserRecipe>()
    val recipes get() = _recipes.toList()

    fun register(recipe: LaserRecipe) {
        _recipes.add(recipe)
        _recipes.sortWith(RECIPE_COMPARATOR)
    }

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
}