package com.github.trc.clayium.common.recipe.registry

import com.github.trc.clayium.common.recipe.LaserRecipe
import com.github.trc.clayium.common.recipe.builder.LaserRecipeBuilder
import net.minecraft.block.Block

class LaserRecipeRegistry {
    private val _recipes = mutableListOf<LaserRecipe>()
    val recipes get() = _recipes.toList()
    fun register(input: Block, output: Block, energyMin: Double, energyMax: Double, requiredEnergy: Double) {
        _recipes.add(LaserRecipe(input, output, energyMin, energyMax, requiredEnergy))
        _recipes.sortWith(RECIPE_COMPARATOR)
    }

    fun register(input: Block, output: Block, energyMin: Double, requiredEnergy: Double) {
        _recipes.add(LaserRecipe(input, output, energyMin, null, requiredEnergy))
        _recipes.sortWith(RECIPE_COMPARATOR)
    }

    fun register(create: LaserRecipeBuilder.() -> Unit) {
        val builder = LaserRecipeBuilder()
        builder.create()
        _recipes.add(builder.build())
        _recipes.sortWith(RECIPE_COMPARATOR)
    }
    fun getRecipe(input: Block, energy: Double): LaserRecipe? {
        for (recipe in _recipes) {
            if (recipe.matches(input, energy)) {
                return recipe
            }
        }
        return null
    }

    companion object {
        private val RECIPE_COMPARATOR = compareBy<LaserRecipe> {
            it.energyMin
        }.reversed()
    }
}