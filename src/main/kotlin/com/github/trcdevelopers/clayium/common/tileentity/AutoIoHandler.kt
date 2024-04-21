package com.github.trcdevelopers.clayium.common.tileentity

import com.github.trcdevelopers.clayium.common.blocks.machine.MachineIoMode
import net.minecraft.util.EnumFacing

class AutoIoHandler(private val tile: TileEntityMachine) {
    fun isAutoInput(side: EnumFacing): Boolean = tile.getInput(side) != MachineIoMode.NONE
    fun isAutoOutput(side: EnumFacing): Boolean = tile.getOutput(side) != MachineIoMode.NONE
}