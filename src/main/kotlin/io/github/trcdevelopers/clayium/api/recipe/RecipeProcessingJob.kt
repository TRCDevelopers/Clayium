package io.github.trcdevelopers.clayium.api.recipe

import io.github.trcdevelopers.clayium.api.ClayEnergy
import net.minecraft.item.ItemStack

data class RecipeProcessingJob(
    val requiredWork: Long,
    val clayEnergyPerTick: ClayEnergy,
    val outputs: List<ItemStack>,
)