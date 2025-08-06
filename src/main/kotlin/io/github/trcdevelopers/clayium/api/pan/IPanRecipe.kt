package io.github.trcdevelopers.clayium.api.pan

import io.github.trcdevelopers.clayium.api.ClayEnergy
import io.github.trcdevelopers.clayium.common.recipe.ingredient.CRecipeInput
import net.minecraft.item.ItemStack

interface IPanRecipe {
    val ingredients: List<CRecipeInput>
    val results: List<ItemStack>
    val requiredClayEnergy: ClayEnergy
}