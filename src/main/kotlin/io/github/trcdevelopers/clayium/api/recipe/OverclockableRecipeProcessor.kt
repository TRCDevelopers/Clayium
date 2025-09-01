package io.github.trcdevelopers.clayium.api.recipe

import io.github.trcdevelopers.clayium.api.metatileentity.trait.OverclockHandler
import java.util.function.IntSupplier
import java.util.function.LongSupplier

private val ALWAYS_ONE = LongSupplier { 1 }

class OverclockableRecipeProcessor @JvmOverloads constructor(
    private val overclockHandler: OverclockHandler,
    private val progressPerTick: LongSupplier = ALWAYS_ONE,
) : IRecipeProcessor {

    override var requiredProgress: Long = 0
    override var currentProgress: Long = 0

    override val normalizedProgress: Double
        get() = if (requiredProgress <= 0) 0.0 else currentProgress.toDouble() / (requiredProgress.toDouble() + 1.0)


    override var isWorking: Boolean = false
        private set
    override val hasRecipe: Boolean get() = requiredProgress > 0

    // currentProgress starts from 1.
    override val isCompleted get() = currentProgress > requiredProgress

    override fun tick() {
        if (this.hasRecipe) {
            val progress = progressPerTick.asLong * this.overclockHandler.accelerationFactor
            this.currentProgress += progress.toLong()
        }
    }

    override fun set(recipe: IClayiumRecipe) {
        this.requiredProgress = (recipe.duration / this.overclockHandler.compensatedFactor).toLong()
        this.currentProgress = 1
    }

    override fun reset() {
        this.requiredProgress = 0
        this.currentProgress = 0
        this.isWorking = false
    }
}