package io.github.trcdevelopers.clayium.common.recipe.chanced

interface IChancedOutput<T> {
    val result: T
    val chance: Int
}