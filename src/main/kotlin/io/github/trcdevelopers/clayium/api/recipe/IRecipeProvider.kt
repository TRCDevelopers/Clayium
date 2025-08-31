package io.github.trcdevelopers.clayium.api.recipe

import io.github.trcdevelopers.clayium.common.recipe.Recipe
import net.minecraft.item.ItemStack

/**
 * Recipe search logic for [io.github.trcdevelopers.clayium.api.capability.impl.AbstractRecipeLogic].
 */
interface IRecipeProvider {
    val jeiCategories get() = listOfNotNull(jeiCategory)
    /**
     * null for disable JEI page for this logic.
     */
    val jeiCategory: String?

    fun searchRecipe(machineTier: Int, inputs: List<ItemStack>): Recipe?
}