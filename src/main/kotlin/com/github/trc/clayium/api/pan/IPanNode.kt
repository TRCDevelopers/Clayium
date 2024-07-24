package com.github.trc.clayium.api.pan

interface IPanNode : IPanCable {
    fun setNetwork(network: IPan)
    fun resetNetwork()
}