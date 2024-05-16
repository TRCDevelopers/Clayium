package com.github.trcdevelopers.clayium.api.capability

import com.github.trcdevelopers.clayium.api.laser.IClayLaser
import net.minecraft.util.EnumFacing

interface IClayLaserManager {
    val laser: IClayLaser

    /**
     * laser length is world bound.
     */
    val laserLength: Int
    val isActive: Boolean
    fun updateDirection(direction: EnumFacing)
}