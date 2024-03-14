package com.github.trcdevelopers.clayium.common.blocks

import net.minecraft.util.EnumFacing

/**
 * Interface for TileEntities that can be connected to piped machines.
 *
 * This interface is used to determine whether items should be transported to that direction, and whether a pipe should extend to that direction during rendering.
 */
interface IPipeConnectable {
    fun isImporting(side: EnumFacing): Boolean
    fun isExporting(side: EnumFacing): Boolean
}