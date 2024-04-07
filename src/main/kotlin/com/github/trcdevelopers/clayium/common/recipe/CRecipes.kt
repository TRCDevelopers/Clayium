package com.github.trcdevelopers.clayium.common.recipe

import com.github.trcdevelopers.clayium.common.blocks.machine.MachineBlocks
import com.github.trcdevelopers.clayium.common.blocks.clayworktable.ClayWorkTableMethod
import com.github.trcdevelopers.clayium.common.recipe.registry.ClayWorkTableRecipeRegistry
import com.github.trcdevelopers.clayium.common.recipe.registry.SimpleCeRecipeRegistry
import net.minecraft.item.ItemStack

object CRecipes {
    val CLAY_WORK_TABLE = ClayWorkTableRecipeRegistry()

    val BENDING = SimpleCeRecipeRegistry(MachineBlocks.Name.BENDING, 1, 1)

    private val simpleCeRecipes = mapOf(
        MachineBlocks.Name.BENDING to BENDING
    )

    fun getSimpleCeRecipeRegistry(name: String): SimpleCeRecipeRegistry? {
        return simpleCeRecipes[name]
    }

    fun getClayWorkTableRecipe(input: ItemStack, method: ClayWorkTableMethod): ClayWorkTableRecipe? {
        return CLAY_WORK_TABLE.getRecipe(input, method)
    }

    fun getBendingRecipe(input: ItemStack): SimpleCeRecipe? {
        return simpleCeRecipes[MachineBlocks.Name.BENDING]?.getRecipe(input)
    }
}