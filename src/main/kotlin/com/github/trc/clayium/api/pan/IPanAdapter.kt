package com.github.trc.clayium.api.pan

interface IPanAdapter {
    fun getEntries(): Set<IPanEntry>
    fun setCore(network: IPanNotifiable)
    fun coreRemoved()
}