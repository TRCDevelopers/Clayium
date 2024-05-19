package com.github.trcdevelopers.clayium.api.laser

import net.minecraft.util.EnumFacing

data class ClayLaser(
    override val laserDirection: EnumFacing,
    override val laserRed: Int,
    override val laserGreen: Int,
    override val laserBlue: Int,
    override val laserAge: Int = 0,
) : IClayLaser {

    override val laserEnergy: Int = 1

    fun changeDirection(direction: EnumFacing): ClayLaser {
        return ClayLaser(direction, laserRed, laserGreen, laserBlue)
    }
}