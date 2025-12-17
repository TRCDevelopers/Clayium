package io.github.trcdevelopers.clayium.api.recipe

import net.minecraft.item.ItemStack

data class RecipeProcessingJob(
    val requiredWork: Long,
    val outputs: List<ItemStack>,
)