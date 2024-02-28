package com.github.trcdevelopers.clayium.common.recipe

import com.github.trcdevelopers.clayium.common.blocks.machine.clayworktable.ClayWorkTableMethod
import net.minecraft.item.ItemStack

object CRecipes {
    private val _clayWorkTable = mutableListOf<ClayWorkTableRecipe>()
    val CLAY_WORK_TABLE get() = _clayWorkTable.toList()

    fun getClayWorkTableRecipe(input: ItemStack, method: ClayWorkTableMethod): ClayWorkTableRecipe? {
        for (recipe in _clayWorkTable) {
            if (recipe.matches(input, method)) {
                return recipe
            }
        }
        return null
    }

    fun addClayWorkTableRecipe(recipe: ClayWorkTableRecipe) {
        _clayWorkTable.add(recipe)
    }
}