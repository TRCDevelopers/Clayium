package com.github.trcdevelopers.clayium.api.capability.impl

import com.github.trcdevelopers.clayium.api.capability.IClayLaser
import net.minecraft.util.EnumFacing

class ClayLaser(
    override val laserDirection: EnumFacing,
    val laserRed: Int,
    val laserGreen: Int,
    val laserBlue: Int,
) : IClayLaser {
    override fun getLaserLength(): Int {
        return 32
    }

    override fun getLaserEnergy(): Double {
        return 1.0
    }

    override fun getLaserRgb(): Int {
        return (laserRed shl 16) or (laserGreen shl 8) or laserBlue
    }
}