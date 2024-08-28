package com.github.trc.clayium.api.capability

import com.github.trc.clayium.api.laser.ClayLaser
import net.minecraft.util.EnumFacing

interface IClayLaserAcceptor {
    /**
     * Called every tick when the laser is irradiating this block.
     * @param irradiatedSide the side that is irradiated by the laser
     * @param laser if null, it means the laser irradiation has stopped.
     */
    fun laserChanged(irradiatedSide: EnumFacing, laser: ClayLaser?)
}