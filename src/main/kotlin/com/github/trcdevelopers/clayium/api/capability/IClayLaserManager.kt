package com.github.trcdevelopers.clayium.api.capability

import com.github.trcdevelopers.clayium.api.laser.IClayLaser
import net.minecraft.util.EnumFacing

interface IClayLaserManager {
    val laser: IClayLaser
    fun updateDirection(direction: EnumFacing)
}