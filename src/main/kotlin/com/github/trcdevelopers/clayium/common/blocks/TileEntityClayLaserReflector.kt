package com.github.trcdevelopers.clayium.common.blocks

import com.github.trcdevelopers.clayium.api.capability.ClayiumTileCapabilities
import com.github.trcdevelopers.clayium.api.capability.IClayLaserAcceptor
import com.github.trcdevelopers.clayium.api.capability.IClayLaserManager
import com.github.trcdevelopers.clayium.api.laser.ClayLaser
import com.github.trcdevelopers.clayium.api.laser.IClayLaser
import net.minecraft.block.material.Material
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.network.NetworkManager
import net.minecraft.network.play.server.SPacketUpdateTileEntity
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.EnumFacing
import net.minecraft.util.ITickable
import net.minecraft.util.math.BlockPos
import net.minecraftforge.common.capabilities.Capability
import net.minecraftforge.common.util.Constants

class TileEntityClayLaserReflector : TileEntity(), ITickable, IClayLaserManager, IClayLaserAcceptor {

    override var laser: ClayLaser = ClayLaser(EnumFacing.NORTH, 0, 0, 0, 1)
    override var laserLength: Int = MAX_LASER_LENGTH
    private var laserTarget: TileEntity? = null
    override val isActive: Boolean get() = receivedLasers.isNotEmpty()

    private val receivedLasers = mutableListOf<IClayLaser>()

    override fun hasCapability(capability: Capability<*>, facing: EnumFacing?): Boolean {
        return capability === ClayiumTileCapabilities.CAPABILITY_CLAY_LASER
                || capability === ClayiumTileCapabilities.CAPABILITY_CLAY_LASER_ACCEPTOR
                || super.hasCapability(capability, facing)
    }

    override fun <T> getCapability(capability: Capability<T>, facing: EnumFacing?): T? {
        return when {
            capability === ClayiumTileCapabilities.CAPABILITY_CLAY_LASER -> {
                ClayiumTileCapabilities.CAPABILITY_CLAY_LASER.cast(this)
            }
            capability === ClayiumTileCapabilities.CAPABILITY_CLAY_LASER_ACCEPTOR -> {
                ClayiumTileCapabilities.CAPABILITY_CLAY_LASER_ACCEPTOR.cast(this)
            }
            else -> super.getCapability(capability, facing)
        }
    }

    override fun update() {
        if (world.isRemote) return
        if (world.worldTime % 2L == 0L) {
            val previousLaserLength = laserLength
            updateLaserLength()
            if (laserLength != previousLaserLength) {
                val state = world.getBlockState(pos)
                world.notifyBlockUpdate(pos, state, state, Constants.BlockFlags.SEND_TO_CLIENTS)
            }
        }
    }

    private fun updateLaserLength() {
        for (i in 1..MAX_LASER_LENGTH) {
            val facing = world.getBlockState(pos).getValue(BlockClayLaserReflector.FACING)
            val targetPos = pos.offset(facing, i)
            if (canGoThroughBlock(targetPos)) continue
            this.laserTarget = world.getTileEntity(targetPos)
                ?.takeIf { it.hasCapability(ClayiumTileCapabilities.CAPABILITY_CLAY_LASER_ACCEPTOR, facing.opposite) }
            this.laserLength = i
            return
        }
        this.laserLength = MAX_LASER_LENGTH
        this.laserTarget = null
    }

    private fun canGoThroughBlock(pos: BlockPos): Boolean {
        val material = this.world.getBlockState(pos).material
        return (material == Material.AIR) || (material == Material.GRASS)
    }

    override fun updateDirection(direction: EnumFacing) {
        this.laser = laser.changeDirection(direction)
    }

    override fun acceptLaser(side: EnumFacing, laser: IClayLaser) {
        this.receivedLasers.add(laser)
        val direction = this.world.getBlockState(this.pos).getValue(BlockClayLaserReflector.FACING)
        this.laser = mergeLasers(this.receivedLasers, direction)
        val state = this.world.getBlockState(pos)
        this.world.notifyBlockUpdate(pos, state, state, Constants.BlockFlags.SEND_TO_CLIENTS)
    }

    override fun laserStopped(side: EnumFacing) {
        this.receivedLasers.removeIf { it.laserDirection == side.opposite }
        val direction = this.world.getBlockState(this.pos).getValue(BlockClayLaserReflector.FACING)
        this.laser = mergeLasers(this.receivedLasers, direction)
        val state = this.world.getBlockState(pos)
        this.world.notifyBlockUpdate(pos, state, state, Constants.BlockFlags.SEND_TO_CLIENTS)
    }

    override fun getUpdatePacket(): SPacketUpdateTileEntity {
        val laserRgb = laser.laserRed shl 16 or laser.laserGreen shl 8 or laser.laserBlue
        return SPacketUpdateTileEntity(
            this.pos, 0,
            NBTTagCompound().apply {
                setInteger("laserRgb", laserRgb)
                setInteger("laserDirection", laser.laserDirection.index)
                setInteger("laserLength", laserLength)
            }
        )
    }

    override fun onDataPacket(net: NetworkManager, pkt: SPacketUpdateTileEntity) {
        val data = pkt.nbtCompound
        val laserRgb = data.getInteger("laserRgb")
        val laserRed = laserRgb shr 16 and 0xFF
        val laserGreen = laserRgb shr 8 and 0xFF
        val laserBlue = laserRgb and 0xFF
        val laserDirection = EnumFacing.byIndex(data.getInteger("laserDirection"))

        this.laser = ClayLaser(laserDirection, laserRed, laserGreen, laserBlue)
    }

    private companion object {
        const val MAX_LASER_AGE = 10
        const val MAX_LASER_LENGTH = 32

        private fun mergeLasers(lasers: List<IClayLaser>, direction: EnumFacing): ClayLaser {
            val maxAge = lasers.maxOf { it.laserAge }
            if (maxAge >= MAX_LASER_AGE) {
                return ClayLaser(
                    direction,
                    lasers.maxOf { it.laserRed },
                    lasers.maxOf { it.laserGreen },
                    lasers.maxOf { it.laserBlue },
                    maxAge,
                )
            } else {
                return ClayLaser(
                    direction,
                    lasers.sumOf { it.laserRed },
                    lasers.sumOf { it.laserGreen },
                    lasers.sumOf { it.laserBlue },
                    maxAge,
                )
            }
        }
    }
}