package io.github.trcdevelopers.clayium.api.recipe

interface RecipeLifecycleHandler {
    /**
     * @return true if the crafting job is started, false otherwise.
     */
    fun tryStartCrafting(job: RecipeProcessingJob): Boolean

    /**
     * Called when the crafting job is completed.
     */
    fun completeCrafting()
}