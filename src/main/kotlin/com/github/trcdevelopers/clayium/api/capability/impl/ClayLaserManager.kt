package com.github.trcdevelopers.clayium.api.capability.impl

import com.github.trcdevelopers.clayium.api.capability.ClayiumDataCodecs
import com.github.trcdevelopers.clayium.api.capability.ClayiumDataCodecs.UPDATE_LASER_ACTIVATION
import com.github.trcdevelopers.clayium.api.capability.ClayiumDataCodecs.UPDATE_LASER
import com.github.trcdevelopers.clayium.api.capability.ClayiumTileCapabilities
import com.github.trcdevelopers.clayium.api.capability.IClayLaserManager
import com.github.trcdevelopers.clayium.api.laser.ClayLaser
import com.github.trcdevelopers.clayium.api.metatileentity.MTETrait
import com.github.trcdevelopers.clayium.api.metatileentity.MetaTileEntity
import net.minecraft.block.material.Material
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.network.PacketBuffer
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.EnumFacing
import net.minecraft.util.math.BlockPos
import net.minecraft.world.IBlockAccess

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

    private var ticked: ULong = 0u
    override var laser: ClayLaser = ClayLaser(EnumFacing.NORTH, laserRed, laserGreen, laserBlue, 1)
    private var laserTarget: TileEntity? = null
    override var isActive = false
        set(value) {
            if (value) {
                laserTarget
                    ?.getCapability(ClayiumTileCapabilities.CAPABILITY_CLAY_LASER_ACCEPTOR, laser.laserDirection.opposite)
                    ?.acceptLaser(laser.laserDirection.opposite, laser)
                field = true
            } else {
                laserTarget
                    ?.getCapability(ClayiumTileCapabilities.CAPABILITY_CLAY_LASER_ACCEPTOR, laser.laserDirection.opposite)
                    ?.laserStopped(laser.laserDirection.opposite)
                field = false
            }
            if (metaTileEntity.world?.isRemote == false) {
                writeCustomData(UPDATE_LASER_ACTIVATION) {
                    writeBoolean(value)
                }
            }
        }

    override fun update() {
        if (metaTileEntity.world?.isRemote == true) return
        if (ticked % 2u == 0u.toULong()) {
            val previousLaserLength = laser.laserLength
            updateLaserLength()
            if (previousLaserLength != laser.laserLength) {
                writeLaserData()
            }
        }
        ticked++
    }

    override fun updateDirection(direction: EnumFacing) {
        laser = laser.changeDirection(direction)
        writeLaserData()
    }

    override fun receiveCustomData(discriminator: Int, buf: PacketBuffer) {
        when (discriminator) {
            UPDATE_LASER -> {
                val length = buf.readVarInt()
                val direction = EnumFacing.byIndex(buf.readVarInt())
                laser = ClayLaser(direction, laserRed, laserGreen, laserBlue, length)
            }
            UPDATE_LASER_ACTIVATION -> {
                isActive = buf.readBoolean()
            }
        }
    }

    override fun writeInitialSyncData(buf: PacketBuffer) {
        buf.writeVarInt(laser.laserLength)
        buf.writeVarInt(laser.laserDirection.index)
        buf.writeBoolean(isActive)
    }

    override fun receiveInitialSyncData(buf: PacketBuffer) {
        val length = buf.readVarInt()
        val direction = EnumFacing.byIndex(buf.readVarInt())
        laser = ClayLaser(direction, 3, 3, 3, length)
        isActive = buf.readBoolean()
    }

    override fun serializeNBT(): NBTTagCompound {
        return NBTTagCompound().apply {
            setByte("laserDirection", laser.laserDirection.index.toByte())
            setInteger("laserLength", laser.laserLength)
            setBoolean("isActive", isActive)
        }
    }

    override fun deserializeNBT(data: NBTTagCompound) {
        laser = ClayLaser(EnumFacing.byIndex(data.getByte("laserDirection").toInt()), laserRed, laserGreen, laserBlue, data.getInteger("laserLength"))
        isActive = data.getBoolean("isActive")
    }

    fun onPlacement(world: IBlockAccess, pos: BlockPos) {
        updateLaserLength()
    }

    private fun updateLaserLength() {
        val pos = metaTileEntity.pos ?: return
        val world = metaTileEntity.world ?: return
        for (i in 1..ClayLaser.MAX_LASER_LENGTH) {
            val targetPos = pos.offset(metaTileEntity.frontFacing, i)
            if (canGoThroughBlock(world, targetPos)) continue
            this.laserTarget = world.getTileEntity(targetPos)
                ?.takeIf { it.hasCapability(ClayiumTileCapabilities.CAPABILITY_CLAY_LASER_ACCEPTOR, metaTileEntity.frontFacing) }
            this.laser = ClayLaser(metaTileEntity.frontFacing, laserRed, laserGreen, laserBlue, i)
            return
        }
        this.laser = ClayLaser(metaTileEntity.frontFacing, laserRed, laserGreen, laserBlue, ClayLaser.MAX_LASER_LENGTH)
    }

    private fun canGoThroughBlock(world: IBlockAccess, pos: BlockPos): Boolean {
        val material = world.getBlockState(pos).material
        return (material == Material.AIR) || (material == Material.GRASS)
    }

    private fun writeLaserData() {
        writeCustomData(UPDATE_LASER) {
            writeVarInt(laser.laserLength)
            writeVarInt(laser.laserDirection.index)
        }
    }
}