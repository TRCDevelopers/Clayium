package com.github.trc.clayium.api.capability.impl

import com.github.trc.clayium.api.capability.ClayiumDataCodecs
import com.github.trc.clayium.api.capability.ClayiumDataCodecs.UPDATE_LASER
import com.github.trc.clayium.api.capability.ClayiumDataCodecs.UPDATE_LASER_ACTIVATION
import com.github.trc.clayium.api.capability.ClayiumTileCapabilities
import com.github.trc.clayium.api.capability.IClayLaserSource
import com.github.trc.clayium.api.laser.ClayLaser
import com.github.trc.clayium.api.metatileentity.MTETrait
import com.github.trc.clayium.api.metatileentity.MetaTileEntity
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.network.PacketBuffer
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.EnumFacing
import net.minecraftforge.common.capabilities.Capability

class ClayLaserSource(
    metaTileEntity: MetaTileEntity,
    private val laserRed: Int = 0,
    private val laserGreen: Int = 0,
    private val laserBlue: Int = 0,
) : MTETrait(metaTileEntity, ClayiumDataCodecs.LASER_CONTROLLER), IClayLaserSource {

    override var laser: ClayLaser = ClayLaser(EnumFacing.NORTH, laserRed, laserGreen, laserBlue, 1)
    override var laserLength: Int = IClayLaserSource.MAX_LASER_LENGTH
        set(value) {
            val syncFlag = field != value && (metaTileEntity.world?.isRemote == false)
            field = value.coerceIn(1, IClayLaserSource.MAX_LASER_LENGTH)
            if (syncFlag) { writeLaserData() }
        }
    private var laserTarget: TileEntity? = null
    override var isActive: Boolean = false
        public set(value) {
            val isValueChanged = field != value
            val syncFlag = !metaTileEntity.isRemote && isValueChanged
            field = value
            if (!isValueChanged) return

            if (value) {
                laserTarget
                    ?.takeUnless { it.isInvalid }
                    ?.getCapability(ClayiumTileCapabilities.CLAY_LASER_ACCEPTOR, laser.direction.opposite)
                    ?.laserChanged(laser.direction.opposite, laser)
            } else {
                laserTarget
                    ?.takeUnless { it.isInvalid }
                    ?.getCapability(ClayiumTileCapabilities.CLAY_LASER_ACCEPTOR, laser.direction.opposite)
                    ?.laserChanged(laser.direction.opposite, null)
            }
            if (syncFlag) {
                writeCustomData(UPDATE_LASER_ACTIVATION) {
                    writeBoolean(value)
                }
            }
        }

    override fun update() {
        if (metaTileEntity.isRemote || !this.isActive) return
        val world = metaTileEntity.world ?: return
        val pos = metaTileEntity.pos ?: return
        val (laserLength, laserTarget) = updateLengthAndTarget(world, pos, laserTarget) {
            writeLaserData()
        }
        this.laserLength = laserLength
        this.laserTarget = laserTarget
    }

    override fun receiveCustomData(discriminator: Int, buf: PacketBuffer) {
        when (discriminator) {
            UPDATE_LASER -> {
                this.laserLength = buf.readVarInt()
                val direction = EnumFacing.byIndex(buf.readVarInt())
                laser = ClayLaser(direction, laserRed, laserGreen, laserBlue, 1)
            }
            UPDATE_LASER_ACTIVATION -> {
                isActive = buf.readBoolean()
            }
        }
    }

    fun updateDirection(direction: EnumFacing) {
        this.laser = this.laser.changeDirection(direction)
        writeLaserData()
    }

    override fun onRemoval() {
        laserTarget?.takeUnless { it.isInvalid }
            ?.getCapability(ClayiumTileCapabilities.CLAY_LASER_ACCEPTOR, laser.direction.opposite)
            ?.laserChanged(laser.direction.opposite, null)
        writeLaserData()
    }


    override fun writeInitialSyncData(buf: PacketBuffer) {
        buf.writeVarInt(laserLength)
        buf.writeVarInt(laser.direction.index)
        buf.writeBoolean(isActive)
    }

    override fun receiveInitialSyncData(buf: PacketBuffer) {
        val length = buf.readVarInt()
        val direction = EnumFacing.byIndex(buf.readVarInt())
        laserLength = length
        laser = ClayLaser(direction, laserRed, laserGreen, laserBlue, length)
        isActive = buf.readBoolean()
    }

    private fun writeLaserData() {
        writeCustomData(UPDATE_LASER) {
            writeVarInt(laserLength)
            writeVarInt(laser.direction.index)
        }
    }

    override fun serializeNBT(): NBTTagCompound {
        return NBTTagCompound().apply {
            setByte("laserDirection", laser.direction.index.toByte())
            setInteger("laserLength", laserLength)
            setBoolean("isActive", isActive)
        }
    }

    override fun deserializeNBT(data: NBTTagCompound) {
        laser = ClayLaser(EnumFacing.byIndex(data.getByte("laserDirection").toInt()), laserRed, laserGreen, laserBlue)
        laserLength = data.getInteger("laserLength")
        isActive = data.getBoolean("isActive")
    }

    override fun <T> getCapability(capability: Capability<T>, facing: EnumFacing?): T? {
        return if (capability === ClayiumTileCapabilities.CLAY_LASER_SOURCE) {
            capability.cast(this)
        } else {
            super.getCapability(capability, facing)
        }
    }
}