package io.github.trcdevelopers.clayium.api.recipe

import io.github.trcdevelopers.clayium.api.ClayEnergy
import io.github.trcdevelopers.clayium.api.recipe.input.CRecipeInputV2

data class RecipeV2(
    override val itemInputs: List<CRecipeInputV2>,
    override val outputs: IRecipeOutputs,
    override val duration: Long,
    override val cePerTick: ClayEnergy,
    /**
     * if `machine.tier.numeric < recipe.tier`, then the recipe is not matched
     */
    override val recipeTier: Int,
    override val priority: Int = 0,
) : IClayiumRecipe {}