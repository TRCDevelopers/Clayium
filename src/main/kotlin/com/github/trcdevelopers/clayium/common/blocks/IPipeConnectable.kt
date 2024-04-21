package com.github.trcdevelopers.clayium.common.blocks

import com.github.trcdevelopers.clayium.common.blocks.machine.MachineIoMode
import net.minecraft.util.EnumFacing

/**
 * Interface for TileEntities that can be connected to piped machines.
 *
 * This interface is used to determine whether a pipe should extend to that direction during rendering.
 */
interface IPipeConnectable {
    fun acceptInputFrom(side: EnumFacing): Boolean
    fun acceptOutputTo(side: EnumFacing): Boolean

    fun isAutoInput(side: EnumFacing): Boolean = acceptInputFrom(side)
    fun isAutoOutput(side: EnumFacing): Boolean = acceptOutputTo(side)
}