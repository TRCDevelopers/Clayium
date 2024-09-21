package com.github.trc.clayium.api.recipe

import com.github.trc.clayium.common.recipe.Recipe
import net.minecraft.item.ItemStack

/**
 * Recipe search logic for [com.github.trc.clayium.api.capability.impl.AbstractRecipeLogic].
 */
interface IRecipeProvider {
    val jeiCategories get() = listOfNotNull(jeiCategory)
    /**
     * null for disable JEI page for this logic.
     */
    val jeiCategory: String?

    fun searchRecipe(machineTier: Int, inputs: List<ItemStack>): Recipe?
}