package io.github.trcdevelopers.clayium.api.recipe

import net.minecraft.item.ItemStack

interface RecipeJobProvider {
    fun provide(
        machineTier: Int,
        inputs: List<ItemStack>,
    ): RecipeProcessingJob?
}