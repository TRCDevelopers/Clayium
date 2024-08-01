package com.github.trc.clayium.api.pan

interface IPanNotifiable {
    /**
     * Notifies the PAN that there have been changes to the network, such as node joining or leaving.
     *
     * Calling this method will cause the PAN to re-search the world for nodes and update the network.
     */
    fun notifyNetwork()
}