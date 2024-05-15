package com.github.trcdevelopers.clayium.api.capability

import com.github.trcdevelopers.clayium.api.laser.IClayLaser
import net.minecraft.util.EnumFacing

interface IClayLaserAcceptor {
    fun acceptLaserFrom(side: EnumFacing, laser: IClayLaser)
}