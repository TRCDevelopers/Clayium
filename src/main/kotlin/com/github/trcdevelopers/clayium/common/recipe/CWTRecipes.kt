package com.github.trcdevelopers.clayium.common.recipe

import com.github.trcdevelopers.clayium.common.blocks.machine.MachineBlocks
import com.github.trcdevelopers.clayium.common.blocks.clayworktable.ClayWorkTableMethod
import com.github.trcdevelopers.clayium.common.recipe.registry.ClayWorkTableRecipeRegistry
import com.github.trcdevelopers.clayium.common.recipe.registry.SimpleCeRecipeRegistry
import net.minecraft.item.ItemStack

object CWTRecipes {
    val CLAY_WORK_TABLE = ClayWorkTableRecipeRegistry()

    fun getClayWorkTableRecipe(input: ItemStack, method: ClayWorkTableMethod): ClayWorkTableRecipe? {
        return CLAY_WORK_TABLE.getRecipe(input, method)
    }
}