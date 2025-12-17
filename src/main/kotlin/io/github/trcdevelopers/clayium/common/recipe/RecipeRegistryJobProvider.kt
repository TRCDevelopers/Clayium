package io.github.trcdevelopers.clayium.common.recipe

import io.github.trcdevelopers.clayium.api.ClayEnergy
import io.github.trcdevelopers.clayium.api.recipe.RecipeJobProvider
import io.github.trcdevelopers.clayium.api.recipe.RecipeProcessingJob
import io.github.trcdevelopers.clayium.common.recipe.registry.RecipeRegistry
import net.minecraft.item.ItemStack
import kotlin.math.pow

open class RecipeRegistryJobProvider(
    private val registry: RecipeRegistry<*>,
) : RecipeJobProvider {
    override fun provide(machineTier: Int, inputs: List<ItemStack>, overclockCFactor: Double): RecipeProcessingJob? {
        val recipe = registry.searchRecipe(machineTier, inputs) ?: return null

        val (cePerTick, duration) = applyOverclock(recipe.cePerTick, recipe.duration, overclockCFactor)

        return RecipeProcessingJob(
            requiredWork = duration,
            clayEnergyPerTick = ClayEnergy(cePerTick),
            outputs = recipe.copyOutputs(),
        )
    }

    protected open fun applyOverclock(cePerTick: ClayEnergy, duration: Long, compensatedFactor: Double): LongArray {
        val rawCEt = cePerTick.energy * compensatedFactor.pow(1.5)
        val durationOCed = (duration / compensatedFactor)
        return longArrayOf(rawCEt.toLong(), durationOCed.toLong())
    }
}