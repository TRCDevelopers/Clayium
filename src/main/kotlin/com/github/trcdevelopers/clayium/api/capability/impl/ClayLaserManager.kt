package com.github.trcdevelopers.clayium.api.capability.impl

import com.github.trcdevelopers.clayium.api.capability.ClayiumDataCodecs
import com.github.trcdevelopers.clayium.api.capability.ClayiumDataCodecs.UPDATE_LASER_DIRECTION
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
import net.minecraft.world.World

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
        }

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

    fun onPlacement(world: World, pos: BlockPos) {
        if (world.isRemote) return
        for (i in 1..MAX_LASER_LENGTH) {
            val targetPos = pos.offset(metaTileEntity.frontFacing, i)
            if (canGoThroughBlock(world, targetPos)) continue
            this.laserTarget = world.getTileEntity(targetPos)
        }
    }

    private fun canGoThroughBlock(world: IBlockAccess, pos: BlockPos): Boolean {
        val material = world.getBlockState(pos).material
        return (material == Material.AIR) || (material == Material.GRASS)
    }

    private companion object {
        private const val MAX_LASER_LENGTH = 32
    }
}