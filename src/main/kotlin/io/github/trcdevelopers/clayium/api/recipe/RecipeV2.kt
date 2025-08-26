package io.github.trcdevelopers.clayium.api.recipe

import io.github.trcdevelopers.clayium.api.ClayEnergy
import io.github.trcdevelopers.clayium.common.recipe.ingredient.CRecipeInput

data class RecipeV2(
    val inputs: List<CRecipeInput>,
    val outputs: IRecipeOutputs,
    val duration: Long,
    val cePerTick: ClayEnergy,
    /**
     * if `machine.tier.numeric < recipe.tier`, then the recipe is not matched
     */
    val recipeTier: Int,
    val priority: Int = 0,
) {}