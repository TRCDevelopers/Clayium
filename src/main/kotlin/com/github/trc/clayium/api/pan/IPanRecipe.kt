package com.github.trc.clayium.api.pan

import com.github.trc.clayium.api.ClayEnergy
import com.github.trc.clayium.common.recipe.ingredient.CRecipeInput
import net.minecraft.item.ItemStack

interface IPanRecipe {
    val ingredients: List<CRecipeInput>
    val results: List<ItemStack>
    val requiredClayEnergy: ClayEnergy
}