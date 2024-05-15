package com.github.trcdevelopers.clayium.api.laser

import net.minecraft.util.EnumFacing

class ClayLaser(
    override val laserDirection: EnumFacing,
    val laserRed: Int,
    val laserGreen: Int,
    val laserBlue: Int,
    override val laserLength: Int,
) : IClayLaser {

    init {
        require(laserLength in 0..MAX_LASER_LENGTH) { "laserLength must be in range 0..$MAX_LASER_LENGTH" }
    }

    override val laserEnergy: Int = 1
    override val laserRgb: Int
        get() = (laserRed shl 16) or (laserGreen shl 8) or laserBlue

    fun changeDirection(direction: EnumFacing): ClayLaser {
        return ClayLaser(direction, laserRed, laserGreen, laserBlue, laserLength)
    }

    companion object {
        const val MAX_LASER_LENGTH = 32
    }
}