package io.github.trcdevelopers.clayium.common.recipe.registry

import io.github.trcdevelopers.clayium.common.blocks.clayworktable.ClayWorkTableMethod
import io.github.trcdevelopers.clayium.common.recipe.ClayWorkTableRecipe
import io.github.trcdevelopers.clayium.common.recipe.RecipeInput
import net.minecraft.item.ItemStack

class ClayWorkTableRecipeRegistry {
    private val _recipes = mutableListOf<ClayWorkTableRecipe>()
    val recipes get() = _recipes.toList()

    fun register(input: RecipeInput, primaryOutput: ItemStack, secondaryOutput: ItemStack = ItemStack.EMPTY, method: ClayWorkTableMethod, clicks: Int) {
        _recipes.add(ClayWorkTableRecipe(input, primaryOutput, secondaryOutput, method, clicks))
        _recipes.sortWith(RECIPE_COMPARATOR)
    }

    fun register(create: ClayWorkTableRecipe.Builder.() -> Unit) {
        val builder = ClayWorkTableRecipe.Builder()
        builder.create()
        _recipes.add(builder.build())
        _recipes.sortWith(RECIPE_COMPARATOR)
    }

    fun getRecipe(input: ItemStack, method: ClayWorkTableMethod): ClayWorkTableRecipe? {
        for (recipe in _recipes) {
            if (recipe.matches(input, method)) {
                return recipe
            }
        }
        return null
    }

    companion object {
        private val RECIPE_COMPARATOR = compareBy<ClayWorkTableRecipe> {
            it.input.amount
        }.reversed()
    }
}