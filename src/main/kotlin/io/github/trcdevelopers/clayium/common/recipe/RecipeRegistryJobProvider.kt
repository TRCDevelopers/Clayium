package io.github.trcdevelopers.clayium.common.recipe

import io.github.trcdevelopers.clayium.api.recipe.RecipeJobProvider
import io.github.trcdevelopers.clayium.api.recipe.RecipeProcessingJob
import io.github.trcdevelopers.clayium.common.recipe.registry.RecipeRegistry
import net.minecraft.item.ItemStack

class RecipeRegistryJobProvider(
    private val registry: RecipeRegistry<*>,
) : RecipeJobProvider {
    override fun provide(machineTier: Int, inputs: List<ItemStack>): RecipeProcessingJob? {
        val recipe = registry.searchRecipe(machineTier, inputs) ?: return null
        return RecipeProcessingJob(
            requiredWork = recipe.duration,
            outputs = recipe.copyOutputs(),
        )
    }
}