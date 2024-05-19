package com.github.trcdevelopers.clayium.common.blocks

import com.github.trcdevelopers.clayium.api.capability.ClayiumTileCapabilities
import com.github.trcdevelopers.clayium.api.capability.IClayLaserAcceptor
import com.github.trcdevelopers.clayium.api.capability.IClayLaserSource
import com.github.trcdevelopers.clayium.api.laser.ClayLaser
import com.github.trcdevelopers.clayium.api.laser.IClayLaser
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap
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

class TileEntityClayLaserReflector : TileEntity(), ITickable, IClayLaserSource, IClayLaserAcceptor {

    override var laser: ClayLaser = ClayLaser(EnumFacing.NORTH, 0, 0, 0, 1)
    override var laserLength: Int = MAX_LASER_LENGTH
        set(value) {
            val syncFlag = field != value && !world.isRemote
            field = value.coerceIn(1, MAX_LASER_LENGTH)
            if (syncFlag) { notifyWorld() }
        }
    private var laserTarget: TileEntity? = null
    override var isActive: Boolean = false

    /**
     * flag to notify [laserTarget] that the irradiating laser has changed.
     */
    private var laserChangedAndToNotify = false

    private val receivedLasers = Object2ObjectOpenHashMap<EnumFacing, IClayLaser>()

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
        val previousLaserLength = laserLength
        updateLaserLength()
        updateTargetInstance()
        if (laserChangedAndToNotify) {
            laserChangedAndToNotify = false
            laserTarget?.takeUnless { it.isInvalid }
                ?.getCapability(ClayiumTileCapabilities.CAPABILITY_CLAY_LASER_ACCEPTOR, laser.laserDirection.opposite)
                ?.laserChanged(laser.laserDirection.opposite, laser)
        }
        if (laserLength != previousLaserLength) {
            val state = world.getBlockState(pos)
            world.notifyBlockUpdate(pos, state, state, Constants.BlockFlags.SEND_TO_CLIENTS)
        }
    }

    override fun invalidate() {
        val laserDirection = this.laser.laserDirection
        this.laserTarget?.takeUnless { it.isInvalid }
            ?.getCapability(ClayiumTileCapabilities.CAPABILITY_CLAY_LASER_ACCEPTOR, laserDirection.opposite)
            ?.laserChanged(laserDirection.opposite, null)
        super.invalidate()
    }

    private fun updateLaserLength() {
        for (i in 1..MAX_LASER_LENGTH) {
            val facing = world.getBlockState(pos).getValue(BlockClayLaserReflector.FACING)
            val targetPos = pos.offset(facing, i)
            if (canGoThroughBlock(targetPos)) continue
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

    private fun updateTargetInstance() {
        val targetSide = this.world.getBlockState(this.pos).getValue(BlockClayLaserReflector.FACING).opposite
        val previousTarget = this.laserTarget
        this.laserTarget = this.world.getTileEntity(this.pos.offset(this.laser.laserDirection, this.laserLength))
            ?.takeIf { it.hasCapability(ClayiumTileCapabilities.CAPABILITY_CLAY_LASER_ACCEPTOR, targetSide) }
        if (previousTarget != laserTarget) {
            previousTarget?.takeUnless { it.isInvalid }
                ?.getCapability(ClayiumTileCapabilities.CAPABILITY_CLAY_LASER_ACCEPTOR, targetSide)
                ?.laserChanged(targetSide, null)
            laserTarget?.getCapability(ClayiumTileCapabilities.CAPABILITY_CLAY_LASER_ACCEPTOR, targetSide)
                ?.laserChanged(targetSide, this.laser)
            val state = this.world.getBlockState(this.pos)
            this.world.notifyBlockUpdate(this.pos, state, state, Constants.BlockFlags.SEND_TO_CLIENTS)
        }
    }

    override fun updateDirection(direction: EnumFacing) {
        this.laser = laser.changeDirection(direction)
    }

    override fun laserChanged(irradiatedSide: EnumFacing, laser: IClayLaser?) {
        if (laser == null) {
            this.receivedLasers.remove(irradiatedSide)
        } else {
            this.receivedLasers[irradiatedSide] = laser
        }
        val state = this.world.getBlockState(this.pos)
        val mergedLaserDirection = state.getValue(BlockClayLaserReflector.FACING)
        this.laser = mergeLasers(this.receivedLasers.values, mergedLaserDirection)
        this.isActive = this.receivedLasers.isNotEmpty()
        this.laserChangedAndToNotify = true
        this.world.notifyBlockUpdate(pos, state, state, Constants.BlockFlags.SEND_TO_CLIENTS)
    }

    override fun getUpdatePacket(): SPacketUpdateTileEntity {
        val laserRgb = (laser.laserRed shl 16) or (laser.laserGreen shl 8) or laser.laserBlue
        return SPacketUpdateTileEntity(
            this.pos, 0,
            NBTTagCompound().apply {
                setInteger("laserRgb", laserRgb)
                setInteger("laserDirection", laser.laserDirection.index)
                setInteger("laserLength", laserLength)
                setBoolean("isActive", isActive)
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
        this.laserLength = data.getInteger("laserLength")
        this.isActive = data.getBoolean("isActive")
    }

    private fun notifyWorld() {
        val state = world.getBlockState(pos)
        world.notifyBlockUpdate(pos, state, state, Constants.BlockFlags.SEND_TO_CLIENTS)
    }

    private companion object {
        const val MAX_LASER_AGE = 10
        const val MAX_LASER_LENGTH = 32

        private fun mergeLasers(lasers: Collection<IClayLaser>, direction: EnumFacing): ClayLaser {
            if (lasers.isEmpty()) return ClayLaser(direction, 0, 0, 0)
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
                    maxAge + 1,
                )
            }
        }
    }
}