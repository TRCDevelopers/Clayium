package com.github.trc.clayium.common.blocks

import com.github.trc.clayium.api.capability.ClayiumDataCodecs.UPDATE_LASER
import com.github.trc.clayium.api.capability.ClayiumDataCodecs.UPDATE_LASER_LENGTH
import com.github.trc.clayium.api.capability.ClayiumTileCapabilities
import com.github.trc.clayium.api.capability.IClayLaserAcceptor
import com.github.trc.clayium.api.capability.IClayLaserSource
import com.github.trc.clayium.api.capability.impl.ClayLaserSource
import com.github.trc.clayium.api.laser.ClayLaser
import com.github.trc.clayium.api.laser.readClayLaser
import com.github.trc.clayium.api.laser.writeClayLaser
import com.github.trc.clayium.api.metatileentity.SyncedTileEntityBase
import com.github.trc.clayium.api.metatileentity.interfaces.IWorldObject
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap
import net.minecraft.network.PacketBuffer
import net.minecraft.util.EnumFacing
import net.minecraft.util.ITickable
import net.minecraft.util.math.AxisAlignedBB
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import net.minecraftforge.common.capabilities.Capability

class TileEntityClayLaserReflector : SyncedTileEntityBase(), ITickable, IClayLaserSource, IClayLaserAcceptor, IWorldObject {

    override var irradiatingLaser: ClayLaser? = null
    override var length: Int = 0
        private set(value) {
            val syncFlag = !world.isRemote && (value != field)
            field = value
            if (syncFlag) {
                writeCustomData(UPDATE_LASER_LENGTH) {
                    writeVarInt(value)
                }
            }
        }
    private val laserManager = ClayLaserSource(this)
    private val receivedLasers = Object2ObjectOpenHashMap<EnumFacing, ClayLaser>()

    override val worldObj: World? get() = world
    override val position: BlockPos? get() = pos

    override val direction
        get() = world.getBlockState(pos).getValue(BlockClayLaserReflector.FACING)

    override fun update() {
        if (world.isRemote) return
        val lastLaser = this.irradiatingLaser
        val laser = mergeLasers(this.receivedLasers.values)
        this.irradiatingLaser = laser
        if (laser == null) {
            this.laserManager.stopIrradiation()
        } else {
            this.length = this.laserManager.irradiateLaser(this.direction, laser)
        }
        if (lastLaser != laser) {
            this.markDirty()
            writeLaser(laser)
        }
    }

    override fun invalidate() {
        super.invalidate()
        this.laserManager.stopIrradiation()
    }

    override fun laserChanged(irradiatedSide: EnumFacing, laser: ClayLaser?) {
        if (laser == null) {
            this.receivedLasers.remove(irradiatedSide)
        } else {
            this.receivedLasers[irradiatedSide] = laser
        }
    }

    override fun getRenderBoundingBox(): AxisAlignedBB {
        return INFINITE_EXTENT_AABB
    }

    private fun writeLaser(laser: ClayLaser?) {
        writeCustomData(UPDATE_LASER) {
            if (laser == null) {
                writeBoolean(false)
            } else {
                writeBoolean(true)
                writeClayLaser(laser)
            }
        }
    }

    override fun receiveCustomData(discriminator: Int, buf: PacketBuffer) {
        when (discriminator) {
            UPDATE_LASER -> {
                if (buf.readBoolean()) {
                    this.irradiatingLaser = buf.readClayLaser()
                } else {
                    this.irradiatingLaser = null
                }
            }
            UPDATE_LASER_LENGTH -> {
                this.length = buf.readVarInt()
            }
        }
    }

    override fun writeInitialSyncData(buf: PacketBuffer) {
        val laser = this.irradiatingLaser
        if (laser == null) {
            buf.writeBoolean(false)
        } else {
            buf.writeBoolean(true)
            buf.writeClayLaser(laser)
        }
        buf.writeVarInt(this.length)
    }

    override fun receiveInitialSyncData(buf: PacketBuffer) {
        if (buf.readBoolean()) {
            this.irradiatingLaser = buf.readClayLaser()
        }
        this.length = buf.readVarInt()
    }

    override fun hasCapability(capability: Capability<*>, facing: EnumFacing?): Boolean {
        return capability === ClayiumTileCapabilities.CLAY_LASER_SOURCE
                || capability === ClayiumTileCapabilities.CLAY_LASER_ACCEPTOR
                || super.hasCapability(capability, facing)
    }

    override fun <T> getCapability(capability: Capability<T>, facing: EnumFacing?): T? {
        return when {
            capability === ClayiumTileCapabilities.CLAY_LASER_SOURCE -> capability.cast(this)
            capability === ClayiumTileCapabilities.CLAY_LASER_ACCEPTOR -> capability.cast(this)
            else -> super.getCapability(capability, facing)
        }
    }

    override fun markDirty() { // IMarkDirty
        super.markDirty()
    }

    private companion object {
        const val MAX_LASER_AGE = 10

        private fun mergeLasers(lasers: Collection<ClayLaser>): ClayLaser? {
            if (lasers.isEmpty()) return null
            val maxAge = lasers.maxOf { it.age }
            return if (maxAge >= MAX_LASER_AGE) {
                ClayLaser(
                    lasers.maxOf { it.red },
                    lasers.maxOf { it.green },
                    lasers.maxOf { it.blue },
                    maxAge,
                )
            } else {
                ClayLaser(
                    lasers.sumOf { it.red },
                    lasers.sumOf { it.green },
                    lasers.sumOf { it.blue },
                    maxAge + 1,
                )
            }
        }
    }
}