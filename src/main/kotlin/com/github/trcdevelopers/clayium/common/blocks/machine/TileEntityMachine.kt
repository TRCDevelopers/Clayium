package com.github.trcdevelopers.clayium.common.blocks.machine

import net.minecraft.tileentity.TileEntity
import net.minecraft.util.EnumFacing

class TileEntityMachine : TileEntity() {

    var tier = -1
        private set
    var isPipe = false
        private set

    private val _inputs = MutableList(6) { MachineIoMode.NONE }
    val inputs get() = _inputs.toList()
    private val _outputs = MutableList(6) { MachineIoMode.NONE }
    val outputs get() = _outputs.toList()

    private lateinit var validInputModes: List<MachineIoMode>
    private lateinit var validOutputModes: List<MachineIoMode>

    private fun initParams(tier: Int) {

    }

    private fun setValidIoModes(inputModes: List<MachineIoMode>, outputModes: List<MachineIoMode>) {
        this.validInputModes = inputModes
        this.validOutputModes = outputModes
    }

    fun toggleInput(side: EnumFacing) {
        val current = _inputs[side.index]
        val next = validInputModes[(validInputModes.indexOf(current) + 1) % validInputModes.size]
        _inputs[side.index] = next
    }

    fun toggleOutput(side: EnumFacing) {
        val current = _outputs[side.index]
        val next = validOutputModes[(validOutputModes.indexOf(current) + 1) % validOutputModes.size]
        _outputs[side.index] = next
    }

    companion object {
        fun create(tier: Int, validInputModes: List<MachineIoMode>, validOutputModes: List<MachineIoMode>): TileEntityMachine {
            return TileEntityMachine().apply {
                initParams(tier)
                setValidIoModes(validInputModes, validOutputModes)
            }
        }
    }
}