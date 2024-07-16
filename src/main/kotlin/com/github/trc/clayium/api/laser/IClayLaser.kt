package com.github.trc.clayium.api.laser

import net.minecraft.util.EnumFacing

interface IClayLaser {
    val laserDirection: EnumFacing

    val laserRed: Int
    val laserGreen: Int
    val laserBlue: Int

    val laserEnergy: Double
    val laserAge: Int

    fun toInt(): Int {
        return (laserRed shl 16) or (laserGreen shl 8) or laserBlue
    }
}