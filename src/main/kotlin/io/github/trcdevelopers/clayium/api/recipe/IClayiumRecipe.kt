package io.github.trcdevelopers.clayium.api.recipe

import io.github.trcdevelopers.clayium.api.ClayEnergy
import io.github.trcdevelopers.clayium.common.recipe.ingredient.CRecipeInput

interface IClayiumRecipe  {
    val inputs: List<CRecipeInput> // TODO; make it more generic. like IRecipeInput with fluids
    val outputs: IRecipeOutputs
    val duration: Long
    val cePerTick: ClayEnergy
    val recipeTier: Int
    val priority: Int
}