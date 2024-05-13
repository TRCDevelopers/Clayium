package com.github.trcdevelopers.clayium.api.capability.impl

import com.github.trcdevelopers.clayium.api.capability.ClayiumDataCodecs
import com.github.trcdevelopers.clayium.api.capability.ClayiumDataCodecs.UPDATE_LASER_DIRECTION
import com.github.trcdevelopers.clayium.api.capability.IClayLaserManager
import com.github.trcdevelopers.clayium.api.laser.ClayLaser
import com.github.trcdevelopers.clayium.api.metatileentity.MTETrait
import com.github.trcdevelopers.clayium.api.metatileentity.MetaTileEntity
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.network.PacketBuffer
import net.minecraft.util.EnumFacing

class ClayLaserManager(
    metaTileEntity: MetaTileEntity,
    private val laserRed: Int = 0,
    private val laserGreen: Int = 0,
    private val laserBlue: Int = 0,
) : MTETrait(metaTileEntity, ClayiumDataCodecs.LASER_CONTROLLER), IClayLaserManager {

    /**
     * create a new [ClayLaserManager] with the same strength for all colors
     * @param laserStrength the strength of the laser
     */
    constructor(metaTileEntity: MetaTileEntity, laserStrength: Int) : this(metaTileEntity, laserStrength, laserStrength, laserStrength)

    override var laser = ClayLaser(metaTileEntity.frontFacing, laserRed, laserGreen, laserBlue)

    override fun updateDirection(direction: EnumFacing) {
        laser = ClayLaser(direction, laserRed, laserGreen, laserBlue)
        writeCustomData(UPDATE_LASER_DIRECTION) {
            writeVarInt(direction.index)
        }
    }

    override fun receiveCustomData(discriminator: Int, buf: PacketBuffer) {
        when (discriminator) {
            UPDATE_LASER_DIRECTION -> {
                val direction = EnumFacing.byIndex(buf.readVarInt())
                laser = ClayLaser(direction, 3, 3, 3)
            }
        }
    }

    override fun writeInitialSyncData(buf: PacketBuffer) {
        buf.writeVarInt(laser.laserDirection.index)
    }

    override fun receiveInitialSyncData(buf: PacketBuffer) {
        val direction = EnumFacing.byIndex(buf.readVarInt())
        laser = ClayLaser(direction, 3, 3, 3)
        println(direction)
    }

    override fun serializeNBT(): NBTTagCompound {
        return NBTTagCompound().apply {
            setByte("laserDirection", laser.laserDirection.index.toByte())
        }
    }

    override fun deserializeNBT(data: NBTTagCompound) {
        laser = ClayLaser(EnumFacing.byIndex(data.getByte("laserDirection").toInt()), laserRed, laserGreen, laserBlue)
    }
}