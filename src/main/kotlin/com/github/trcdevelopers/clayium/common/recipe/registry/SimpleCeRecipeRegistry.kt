package com.github.trcdevelopers.clayium.common.recipe.registry

import com.github.trcdevelopers.clayium.common.recipe.SimpleCeRecipe
import net.minecraft.item.ItemStack

class SimpleCeRecipeRegistry(
    private val inputSize: Int,
    private val outputSize: Int,
) {
    private val _recipes = mutableListOf<SimpleCeRecipe>()
    val recipes get() = _recipes.toList()

    fun register(create: SimpleCeRecipe.Builder.() -> Unit) {
        val builder = SimpleCeRecipe.Builder(inputSize, outputSize)
        builder.create()
        _recipes.add(builder.build())
    }

    fun getRecipe(vararg inputs: ItemStack): SimpleCeRecipe? {
        for (recipe in _recipes) {
            if (recipe.matches(*inputs)) {
                return recipe
            }
        }
        return null
    }
}