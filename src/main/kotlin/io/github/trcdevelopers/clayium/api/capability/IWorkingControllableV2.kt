package io.github.trcdevelopers.clayium.api.capability

/**
 * Capability interface for TileEntities.
 * Used in [io.github.trcdevelopers.clayium.common.metatileentities.multiblock.RedstoneProxyMetaTileEntity]
 * to control machine working state.
 */
interface IWorkingControllableV2 {
    /**
     * This can proceed if the conditions are met.
     *
     * This is like an ON/OFF switch.
     */
    var isWorkingEnabled: Boolean
}