package com.github.trcdevelopers.clayium.common.recipe.registry

import com.github.trcdevelopers.clayium.common.blocks.machine.clayworktable.ClayWorkTableMethod
import com.github.trcdevelopers.clayium.common.recipe.ClayWorkTableRecipe
import com.github.trcdevelopers.clayium.common.recipe.RecipeInput
import net.minecraft.item.ItemStack

class ClayWorkTableRecipeRegistry {
    private val _recipes = mutableListOf<ClayWorkTableRecipe>()
    val recipes get() = _recipes.toList()

    fun register(input: RecipeInput, primaryOutput: ItemStack, secondaryOutput: ItemStack = ItemStack.EMPTY, method: ClayWorkTableMethod, clicks: Int) {
        _recipes.add(ClayWorkTableRecipe(input, primaryOutput, secondaryOutput, method, clicks))
    }

    fun register(create: ClayWorkTableRecipe.Builder.() -> Unit) {
        val builder = ClayWorkTableRecipe.Builder()
        builder.create()
        _recipes.add(builder.build())
    }

    fun getRecipe(input: ItemStack, method: ClayWorkTableMethod): ClayWorkTableRecipe? {
        for (recipe in _recipes) {
            if (recipe.matches(input, method)) {
                return recipe
            }
        }
        return null
    }
}