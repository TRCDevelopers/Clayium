package com.github.trc.clayium.api.capability

import net.minecraft.util.EnumFacing

/**
 * Interface for TileEntities that can be connected to piped machines.
 *
 * This interface is used to determine whether a pipe should extend to that direction during
 * rendering. not used for actual IO handling.
 */
interface IPipeConnectable {
    val pipeConnectionLogic: IPipeConnectionLogic

    fun getPipeConnectionMode(side: EnumFacing): PipeConnectionMode
}
