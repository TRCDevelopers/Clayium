package com.github.trcdevelopers.clayium.api.capability.impl

import com.github.trcdevelopers.clayium.api.capability.ClayiumDataCodecs
import com.github.trcdevelopers.clayium.api.capability.ClayiumDataCodecs.UPDATE_LASER_DIRECTION
import com.github.trcdevelopers.clayium.api.capability.IClayLaserManager
import com.github.trcdevelopers.clayium.api.laser.ClayLaser
import com.github.trcdevelopers.clayium.api.metatileentity.MTETrait
import com.github.trcdevelopers.clayium.api.metatileentity.MetaTileEntity
import net.minecraft.network.PacketBuffer
import net.minecraft.util.EnumFacing

class ClayLaserManager(
    metaTileEntity: MetaTileEntity,
) : MTETrait(metaTileEntity, ClayiumDataCodecs.LASER_CONTROLLER), IClayLaserManager {
    override var laser = ClayLaser(metaTileEntity.frontFacing, 3, 3, 3)

    override fun updateDirection(direction: EnumFacing) {
        laser = ClayLaser(direction, 3, 3, 3)
        writeCustomData(UPDATE_LASER_DIRECTION) {
            writeVarInt(direction.index)
        }
    }

    override fun receiveCustomData(discriminator: Int, buf: PacketBuffer) {
        when (discriminator) {
            UPDATE_LASER_DIRECTION -> {
                val direction = EnumFacing.byIndex(buf.readVarInt())
                laser = ClayLaser(direction, 3, 3, 3)
                println(direction)
            }
        }
    }
}