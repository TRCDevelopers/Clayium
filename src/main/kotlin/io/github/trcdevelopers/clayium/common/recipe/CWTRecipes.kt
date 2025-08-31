package io.github.trcdevelopers.clayium.common.recipe

import io.github.trcdevelopers.clayium.common.blocks.clayworktable.ClayWorkTableMethod
import io.github.trcdevelopers.clayium.common.recipe.registry.ClayWorkTableRecipeRegistry
import net.minecraft.item.ItemStack

object CWTRecipes {
    val CLAY_WORK_TABLE = ClayWorkTableRecipeRegistry()

    fun getClayWorkTableRecipe(input: ItemStack, method: ClayWorkTableMethod): ClayWorkTableRecipe? {
        return CLAY_WORK_TABLE.getRecipe(input, method)
    }
}