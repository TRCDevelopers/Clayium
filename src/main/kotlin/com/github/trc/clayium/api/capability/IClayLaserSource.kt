package com.github.trc.clayium.api.capability

import com.github.trc.clayium.api.laser.IClayLaser
import net.minecraft.util.EnumFacing

interface IClayLaserSource {
    val laser: IClayLaser

    /**
     * laser length is world bound.
     */
    val laserLength: Int
    val isActive: Boolean
    fun updateDirection(direction: EnumFacing)
}