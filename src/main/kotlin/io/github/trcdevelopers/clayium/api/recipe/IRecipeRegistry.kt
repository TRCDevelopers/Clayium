package io.github.trcdevelopers.clayium.api.recipe

import net.minecraft.item.ItemStack

interface IRecipeRegistry {
    val jeiCategories get() = listOfNotNull(jeiCategory)
    /**
     * null for disable JEI page for this logic.
     */
    val jeiCategory: String?

    fun searchRecipe(machineTier: Int, inputs: List<ItemStack>): IClayiumRecipe?
}