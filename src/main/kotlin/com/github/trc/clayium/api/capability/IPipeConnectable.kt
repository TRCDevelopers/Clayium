package com.github.trc.clayium.api.capability

import com.github.trc.clayium.api.util.MachineIoMode
import net.minecraft.util.EnumFacing

/**
 * Interface for TileEntities that can be connected to piped machines.
 *
 * This interface is used to determine whether a pipe should extend to that direction during rendering.
 */
interface IPipeConnectable {
    fun getInput(side: EnumFacing): MachineIoMode
    fun getOutput(side: EnumFacing): MachineIoMode

    fun canImportFrom(side: EnumFacing): Boolean = getInput(side) != MachineIoMode.NONE
    fun canExportTo(side: EnumFacing): Boolean = getOutput(side) != MachineIoMode.NONE

    fun canConnectTo(neighbor: IPipeConnectable, side: EnumFacing): Boolean {
        return (this.canImportFrom(side) && neighbor.canExportTo(side.opposite)) ||
                (this.canExportTo(side) && neighbor.canImportFrom(side.opposite))
    }
}