package com.github.trc.clayium.common.recipe.chanced

interface IChancedOutputLogic {
    fun <R, T : IChancedOutput<R>> roll(chancedOutputs: List<T>): List<R>

    fun passesChance(chance: Int) = chance > 0 && (1..MAX_CHANCE).random() <= chance

    companion object {
        const val MAX_CHANCE = 10_000
    }
}
