package com.github.trcdevelopers.clayium.common.blocks

import com.github.trcdevelopers.clayium.api.capability.IClayLaserAcceptor
import com.github.trcdevelopers.clayium.api.laser.IClayLaser
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.EnumFacing
import net.minecraft.util.ITickable

class TileEntityClayLaserReflector : TileEntity(), ITickable {

    private val lasers = mutableListOf<IClayLaser>()
    private val laserAcceptor = object : IClayLaserAcceptor {
        override fun acceptLaser(side: EnumFacing, laser: IClayLaser) {
            lasers.add(laser)
        }

        override fun laserStopped(side: EnumFacing) {
            lasers.removeIf { it.laserDirection == side }
        }

    }

    override fun update() {
        TODO()
    }

    private fun mergeLasers(): IClayLaser {
        TODO()
    }

    private fun laserChanged() {
        TODO()
    }
}