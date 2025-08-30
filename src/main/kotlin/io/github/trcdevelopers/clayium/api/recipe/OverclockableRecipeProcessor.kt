package io.github.trcdevelopers.clayium.api.recipe

import io.github.trcdevelopers.clayium.api.metatileentity.trait.OverclockHandler
import java.util.function.IntSupplier
import java.util.function.LongSupplier

private val ALWAYS_ONE = LongSupplier { 1 }

class OverclockableRecipeProcessor @JvmOverloads constructor(
    private val overclockHandler: OverclockHandler,
    private val progressPerTick: LongSupplier = ALWAYS_ONE,
) : IRecipeProcessor {

    override var requiredProgress: Int = 0
        private set
    override var currentProgress: Int = 0
        private set
    override var isWorking: Boolean = false
        private set
    override val hasRecipe: Boolean get() = requiredProgress > 0

    override val isCompleted get() = currentProgress >= requiredProgress

    override fun tick() {
        val progress = progressPerTick.asLong * this.overclockHandler.accelerationFactor
    }

    override fun set(requiredProgress: Int) {
        val requiredProgress = requiredProgress / this.overclockHandler.compensatedFactor
    }
}