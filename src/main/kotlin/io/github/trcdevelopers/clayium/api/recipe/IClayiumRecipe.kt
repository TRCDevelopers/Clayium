package io.github.trcdevelopers.clayium.api.recipe

import io.github.trcdevelopers.clayium.api.ClayEnergy
import io.github.trcdevelopers.clayium.api.recipe.input.CItemRecipeInputV2

interface IClayiumRecipe  {
    val itemInputs: List<CItemRecipeInputV2>
    val outputs: IRecipeOutputs
    val duration: Long
    val cePerTick: ClayEnergy
    val recipeTier: Int
    val priority: Int
}