package com.github.trcdevelopers.clayium.api.laser

import net.minecraft.util.EnumFacing

interface IClayLaser {
    val laserDirection: EnumFacing

    val laserRed: Int
    val laserGreen: Int
    val laserBlue: Int

    val laserEnergy: Double
    val laserAge: Int
}