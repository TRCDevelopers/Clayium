package com.github.trcdevelopers.clayium.api.laser

import net.minecraft.util.EnumFacing

interface IClayLaser {
    val laserDirection: EnumFacing
    fun getLaserLength(): Int
    fun getLaserEnergy(): Double
    fun getLaserRgb(): Int
}