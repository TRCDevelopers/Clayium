package com.github.trc.clayium.api.capability

interface IPipeConnectionLogic {
    fun canConnect(thisMode: PipeConnectionMode, neighborMode: PipeConnectionMode): Boolean

    object Machine : IPipeConnectionLogic {
        override fun canConnect(thisMode: PipeConnectionMode, neighborMode: PipeConnectionMode): Boolean {
            if (thisMode == PipeConnectionMode.NONE || neighborMode == PipeConnectionMode.NONE) return false
            if (thisMode == PipeConnectionMode.BOTH || neighborMode == PipeConnectionMode.BOTH) return true
            return thisMode != neighborMode
        }
    }

    object ItemPipe : IPipeConnectionLogic {
        override fun canConnect(thisMode: PipeConnectionMode, neighborMode: PipeConnectionMode): Boolean {
            return neighborMode != PipeConnectionMode.NONE
        }
    }
}