package io.github.trcdevelopers.clayium.api.pan

interface IPanAdapter {
    fun getEntries(): Set<IPanRecipe>
}