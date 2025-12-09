package io.github.trcdevelopers.clayium.common.recipe

import io.github.trcdevelopers.clayium.api.capability.IWorkingControllable
import io.github.trcdevelopers.clayium.api.metatileentity.trait.OverclockHandler

class RecipeProgressTracker(
    private val ocHandler: OverclockHandler,
) : IWorkingControllable {

    private var requiredProgress = 0L
    private var currentProgress = 0L

    private val isProcessingRecipe get() = currentProgress != 0L

    fun update() {
    }

    override var isWorkingEnabled: Boolean
        get() = TODO("Not yet implemented")
        set(value) {}
    override val isWorking: Boolean
        get() = TODO("Not yet implemented")
}