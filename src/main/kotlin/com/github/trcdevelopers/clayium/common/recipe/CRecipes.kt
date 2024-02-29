package com.github.trcdevelopers.clayium.common.recipe

import com.github.trcdevelopers.clayium.common.blocks.machine.clayworktable.ClayWorkTableMethod
import com.github.trcdevelopers.clayium.common.recipe.registry.ClayWorkTableRecipeRegistry
import net.minecraft.item.ItemStack

object CRecipes {
    val CLAY_WORK_TABLE = ClayWorkTableRecipeRegistry()

    fun getClayWorkTableRecipe(input: ItemStack, method: ClayWorkTableMethod): ClayWorkTableRecipe? {
        return CLAY_WORK_TABLE.getRecipe(input, method)
    }
}