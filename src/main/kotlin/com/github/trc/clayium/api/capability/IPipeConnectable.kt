package com.github.trc.clayium.api.capability

import net.minecraft.util.EnumFacing

/**
 * Capability for TileEntities that can be connected to piped machines.
 *
 * This interface is used to determine whether a pipe should extend to that direction **during rendering.
 * not used for actual IO handling.**
 *
 * Actual IO is handled by `IItemHandler` capability.
 */
interface IPipeConnectable {
    val pipeConnectionLogic: IPipeConnectionLogic

    fun getPipeConnectionModeForRendering(side: EnumFacing): PipeConnectionMode
}