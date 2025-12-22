package io.github.trcdevelopers.clayium.api.recipe

import net.minecraft.item.ItemStack

/**
 * Handles the lifecycle events of recipe processing.
 *
 * - Start: Attempts to initiate a crafting job.
 * Aborts if preconditions (resources, inventory space, energy) are not met.
 * Returns a [RecipeProcessingJob] on success, or null on failure.
 *
 * - Complete: Finalizes the job and handles output generation.
 */
interface RecipeLifecycleHandler {
    /**
     * @return RecipeProcessingJob if crafting can start, null otherwise.
     */
    fun tryStartCrafting(machineTier: Int, inputs: List<ItemStack>): RecipeProcessingJob?

    /**
     * Called when the crafting job is completed.
     */
    fun completeCrafting()
}