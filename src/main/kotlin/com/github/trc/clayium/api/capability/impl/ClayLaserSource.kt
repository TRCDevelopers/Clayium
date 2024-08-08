package com.github.trc.clayium.api.capability.impl

import com.github.trc.clayium.api.capability.ClayiumDataCodecs
import com.github.trc.clayium.api.capability.ClayiumDataCodecs.UPDATE_LASER
import com.github.trc.clayium.api.capability.ClayiumDataCodecs.UPDATE_LASER_ACTIVATION
import com.github.trc.clayium.api.capability.ClayiumTileCapabilities
import com.github.trc.clayium.api.capability.IClayLaserSource
import com.github.trc.clayium.api.laser.ClayLaser
import com.github.trc.clayium.api.metatileentity.MTETrait
import com.github.trc.clayium.api.metatileentity.MetaTileEntity
import net.minecraft.block.material.Material
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.network.PacketBuffer
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.EnumFacing
import net.minecraft.util.math.BlockPos
import net.minecraft.world.IBlockAccess

class ClayLaserSource(
    metaTileEntity: MetaTileEntity,
    private val laserRed: Int = 0,
    private val laserGreen: Int = 0,
    private val laserBlue: Int = 0,
) : MTETrait(metaTileEntity, ClayiumDataCodecs.LASER_CONTROLLER), IClayLaserSource {

    override var laser: ClayLaser = ClayLaser(EnumFacing.NORTH, laserRed, laserGreen, laserBlue, 1)
    override var laserLength: Int = MAX_LASER_LENGTH
        set(value) {
            val syncFlag = field != value && (metaTileEntity.world?.isRemote == false)
            field = value.coerceIn(1, MAX_LASER_LENGTH)
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
                    ?.getCapability(ClayiumTileCapabilities.CAPABILITY_CLAY_LASER_ACCEPTOR, laser.laserDirection.opposite)
                    ?.laserChanged(laser.laserDirection.opposite, laser)
            } else {
                laserTarget
                    ?.takeUnless { it.isInvalid }
                    ?.getCapability(ClayiumTileCapabilities.CAPABILITY_CLAY_LASER_ACCEPTOR, laser.laserDirection.opposite)
                    ?.laserChanged(laser.laserDirection.opposite, null)
            }
            if (syncFlag) {
                writeCustomData(UPDATE_LASER_ACTIVATION) {
                    writeBoolean(value)
                }
            }
        }

    override fun update() {
        if (metaTileEntity.world?.isRemote == true || !this.isActive) return
        updateLaserLength()
        updateTargetInstance()
    }

    override fun updateDirection(direction: EnumFacing) {
        laser = laser.changeDirection(direction)
        updateLaserLength()
        updateTargetInstance()
        writeLaserData()
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

    override fun writeInitialSyncData(buf: PacketBuffer) {
        buf.writeVarInt(laserLength)
        buf.writeVarInt(laser.laserDirection.index)
        buf.writeBoolean(isActive)
    }

    override fun receiveInitialSyncData(buf: PacketBuffer) {
        val length = buf.readVarInt()
        val direction = EnumFacing.byIndex(buf.readVarInt())
        laserLength = length
        laser = ClayLaser(direction, laserRed, laserGreen, laserBlue, length)
        isActive = buf.readBoolean()
    }

    override fun serializeNBT(): NBTTagCompound {
        return NBTTagCompound().apply {
            setByte("laserDirection", laser.laserDirection.index.toByte())
            setInteger("laserLength", laserLength)
            setBoolean("isActive", isActive)
        }
    }

    override fun deserializeNBT(data: NBTTagCompound) {
        laser = ClayLaser(EnumFacing.byIndex(data.getByte("laserDirection").toInt()), laserRed, laserGreen, laserBlue)
        laserLength = data.getInteger("laserLength")
        isActive = data.getBoolean("isActive")
    }

    override fun onRemoval() {
        laserTarget?.takeUnless { it.isInvalid }
            ?.getCapability(ClayiumTileCapabilities.CAPABILITY_CLAY_LASER_ACCEPTOR, laser.laserDirection.opposite)
            ?.laserChanged(laser.laserDirection.opposite, null)
        writeLaserData()
    }

    private fun updateLaserLength() {
        val pos = metaTileEntity.pos ?: return
        val world = metaTileEntity.world ?: return
        for (i in 1..MAX_LASER_LENGTH) {
            val targetPos = pos.offset(metaTileEntity.frontFacing, i)
            if (canGoThroughBlock(world, targetPos)) continue
            this.laserLength = i
            return
        }
        this.laserLength = MAX_LASER_LENGTH
        this.laserTarget = null
    }

    private fun updateTargetInstance() {
        val pos = metaTileEntity.pos ?: return
        val world = metaTileEntity.world ?: return
        val targetSide = metaTileEntity.frontFacing.opposite
        val previousTarget = this.laserTarget
        this.laserTarget = world.getTileEntity(pos.offset(this.laser.laserDirection, this.laserLength))
            ?.takeIf { it.hasCapability(ClayiumTileCapabilities.CAPABILITY_CLAY_LASER_ACCEPTOR, targetSide) }
        if (previousTarget != laserTarget) {
            previousTarget?.takeUnless { it.isInvalid }
                ?.getCapability(ClayiumTileCapabilities.CAPABILITY_CLAY_LASER_ACCEPTOR, targetSide)
                ?.laserChanged(targetSide, null)
            if (isActive) {
                laserTarget?.getCapability(ClayiumTileCapabilities.CAPABILITY_CLAY_LASER_ACCEPTOR, targetSide)
                    ?.laserChanged(targetSide, this.laser)
            }
            writeLaserData()
        }
    }

    private fun canGoThroughBlock(world: IBlockAccess, pos: BlockPos): Boolean {
        val material = world.getBlockState(pos).material
        return (material == Material.AIR) || (material == Material.GLASS)
    }

    private fun writeLaserData() {
        writeCustomData(UPDATE_LASER) {
            writeVarInt(laserLength)
            writeVarInt(laser.laserDirection.index)
        }
    }

    private companion object {
        private const val MAX_LASER_LENGTH = 32
    }
}