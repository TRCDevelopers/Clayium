package com.github.trcdevelopers.clayium.common.recipe.registry

import com.github.trcdevelopers.clayium.common.blocks.machine.MachineBlocks
import com.github.trcdevelopers.clayium.common.recipe.builder.SimpleRecipeBuilder

object CRecipes {
    val BENDING = RecipeRegistry(MachineBlocks.Name.BENDING, SimpleRecipeBuilder(), 1, 1)
}