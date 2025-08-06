package io.github.trcdevelopers.clayium.common.pan

import io.github.trcdevelopers.clayium.api.ClayEnergy
import io.github.trcdevelopers.clayium.api.pan.IPanRecipe
import io.github.trcdevelopers.clayium.common.recipe.ingredient.CRecipeInput
import net.minecraft.item.ItemStack

data class PanRecipe(
    override val ingredients: List<CRecipeInput>,
    override val results: List<ItemStack>,
    override val requiredClayEnergy: ClayEnergy
) : IPanRecipe {
    constructor(ingredient: CRecipeInput, result: ItemStack, requiredClayEnergy: ClayEnergy)
            : this(listOf(ingredient), listOf(result), requiredClayEnergy)
}
