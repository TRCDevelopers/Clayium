package com.github.trcdevelopers.clayium.common.blocks.machine.tile

import com.github.trcdevelopers.clayium.common.blocks.TileMachineTemp
import com.github.trcdevelopers.clayium.common.blocks.machine.MachineIoMode
import net.minecraftforge.items.IItemHandler

/**
 * single input with single output
 *
 * todo: add recipe support, implement things
 */
class TileSimpleMachine : TileMachineTemp() {

    override lateinit var autoIoHandler: AutoIoHandler
    override lateinit var itemStackHandler: IItemHandler

    override fun initParams(tier: Int, inputModes: List<MachineIoMode>, outputModes: List<MachineIoMode>) {
        super.initParams(tier, inputModes, outputModes)
        TODO()
    }

    companion object {
        fun create(tier: Int): TileSimpleMachine {
            return TileSimpleMachine().apply {
                initParams(tier, MachineIoMode.Input.SINGLE, MachineIoMode.Output.SINGLE)
            }
        }
    }
}