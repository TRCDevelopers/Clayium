package com.github.trc.clayium.common.recipe

import com.github.trc.clayium.common.blocks.clayworktable.ClayWorkTableMethod
import com.github.trc.clayium.common.recipe.registry.ClayWorkTableRecipeRegistry
import net.minecraft.item.ItemStack

object CWTRecipes {
    val CLAY_WORK_TABLE = ClayWorkTableRecipeRegistry()

    fun getClayWorkTableRecipe(
        input: ItemStack,
        method: ClayWorkTableMethod
    ): ClayWorkTableRecipe? {
        return CLAY_WORK_TABLE.getRecipe(input, method)
    }
}
