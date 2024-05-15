package com.github.trcdevelopers.clayium.api.laser

import net.minecraft.util.EnumFacing

interface IClayLaser {
    val laserDirection: EnumFacing
    val laserLength: Int
    val laserEnergy: Int
    val laserRgb: Int
}