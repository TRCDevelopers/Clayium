package io.github.trcdevelopers.clayium.common.recipe.chanced

data class ChancedOutput<T>(
    override val result: T,
    override val chance: Int
) : IChancedOutput<T>
