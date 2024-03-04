package com.github.trcdevelopers.clayium.common.blocks.machine

import net.minecraft.tileentity.TileEntity

class TileEntityMachine : TileEntity() {

    var tier = -1
        private set
    var isPipe = false
        private set
    private lateinit var inputIter: Iterator<MachineIoMode>
    private lateinit var outputIter: Iterator<MachineIoMode>

    val inputs = List(6) { MachineIoMode.NONE }
    val outputs = List(6) { MachineIoMode.NONE }

    private fun initParams(tier: Int) {

    }

    private fun setIterators(inputIter: Iterator<MachineIoMode>, outputIter: Iterator<MachineIoMode>) {
        this.inputIter = inputIter
        this.outputIter = outputIter
    }

    companion object {
        fun create(tier: Int, inputIter: Iterator<MachineIoMode>, outputIter: Iterator<MachineIoMode>): TileEntityMachine {
            return TileEntityMachine().apply {
                initParams(tier)
                setIterators(inputIter, outputIter)
            }
        }
    }
}