package com.github.trc.clayium.api.capability

import com.github.trc.clayium.api.laser.IClayLaser
import net.minecraft.util.EnumFacing

interface IClayLaserAcceptor {
    /**
     * Called when the side is irradiated with a laser or when there is a change in the irradiated laser.
     *
     * **Note:** Can be called after the block has been destroyed.
     *
     * @param irradiatedSide the side that is irradiated by the laser
     * @param laser if null, it means the laser irradiation has stopped.
     */
    fun laserChanged(irradiatedSide: EnumFacing, laser: IClayLaser?)
}