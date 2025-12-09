package io.github.trcdevelopers.clayium.common.recipe

import io.github.trcdevelopers.clayium.api.capability.IWorkingControllable
import io.github.trcdevelopers.clayium.api.metatileentity.trait.OverclockHandler
import io.github.trcdevelopers.clayium.api.sync.ClayiumSyncManager

open class RecipeProgressTracker(
    syncManager: ClayiumSyncManager,
    private val ocHandler: OverclockHandler,
) : IWorkingControllable {

    private var state by syncManager.enum(State::class.java, State.IDLE)
    private var requiredProgress = 0L
    private var currentProgress = 0L

    private val isProcessingRecipe get() = currentProgress != 0L

    fun updateServer() {
        if (state == State.DISABLED) return

        if (isProcessingRecipe) {
            val rawProgress = this.getProgressPerTick()
            this.currentProgress += (rawProgress.toDouble() * ocHandler.accelerationFactor).toLong()
        }
    }

    fun isCompleted(): Boolean {
        return this.currentProgress > this.requiredProgress
    }

    override val isWorking: Boolean get() = this.state == State.WORKING
    override var isWorkingEnabled: Boolean
        get() = this.state == State.DISABLED
        set(value) {
            if (value) {
                if (isProcessingRecipe) {
                    this.state = State.WORKING
                } else {
                    this.state = State.IDLE
                }
            } else {
                this.state = State.DISABLED
            }
        }

    protected open fun getProgressPerTick(): Long {
        return 1L
    }

    enum class State {
        IDLE, WORKING, DISABLED
    }
}