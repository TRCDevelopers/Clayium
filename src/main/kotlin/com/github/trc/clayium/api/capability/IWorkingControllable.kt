package com.github.trc.clayium.api.capability

/**
 * Capability for workable machines.
 * Used in [com.github.trc.clayium.common.metatileentities.multiblock.RedstoneProxyMetaTileEntity]
 * to determine output Redstone Signal.
 */
interface IWorkingControllable {
    /**
     * This can proceed if the conditions are met.
     *
     * This is like an ON/OFF switch.
     */
    var isWorkingEnabled: Boolean

    /**
     * This is actually working or not.
     * Example: true if this is processing items.
     */
    val isWorking: Boolean
}