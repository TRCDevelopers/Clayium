package io.github.trcdevelopers.clayium.api.recipe

import io.github.trcdevelopers.clayium.api.ClayEnergy

data class RecipeProcessingJob(
    val requiredWork: Long,
    val clayEnergyPerTick: ClayEnergy,
)