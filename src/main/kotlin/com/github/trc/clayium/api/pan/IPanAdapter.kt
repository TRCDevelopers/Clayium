package com.github.trc.clayium.api.pan

interface IPanAdapter {
    fun getEntries(): Set<IPanRecipe>
    fun setCore(network: IPanNotifiable)
    fun coreRemoved()
}