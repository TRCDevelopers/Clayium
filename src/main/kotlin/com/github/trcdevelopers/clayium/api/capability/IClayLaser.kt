package com.github.trcdevelopers.clayium.api.capability

import net.minecraft.util.EnumFacing

interface IClayLaser {
    val laserDirection: EnumFacing
    fun getLaserLength(): Int
    fun getLaserEnergy(): Double
    fun getLaserRgb(): Int
}