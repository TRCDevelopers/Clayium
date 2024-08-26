package com.github.trc.clayium.common.blocks

import com.github.trc.clayium.api.capability.ClayiumTileCapabilities
import com.github.trc.clayium.api.capability.IClayLaserAcceptor
import com.github.trc.clayium.api.capability.IClayLaserSource
import com.github.trc.clayium.api.laser.ClayLaser
import com.github.trc.clayium.api.laser.IClayLaser
import com.github.trc.clayium.api.util.takeIfValid
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.network.NetworkManager
import net.minecraft.network.play.server.SPacketUpdateTileEntity
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.EnumFacing
import net.minecraft.util.ITickable
import net.minecraft.util.math.AxisAlignedBB
import net.minecraftforge.common.capabilities.Capability
import net.minecraftforge.common.util.Constants

class TileEntityClayLaserReflector : TileEntity(), ITickable, IClayLaserSource, IClayLaserAcceptor {

    override var laser: ClayLaser = ClayLaser(EnumFacing.NORTH, 0, 0, 0, 1)
    override var laserLength: Int = IClayLaserSource.MAX_LASER_LENGTH
        set(value) {
            val syncFlag = field != value && !world.isRemote
            field = value.coerceIn(1, IClayLaserSource.MAX_LASER_LENGTH)
            if (syncFlag) { notifyWorld() }
        }
    private var laserTarget: TileEntity? = null
    override var isActive: Boolean = false

    private val receivedLasers = Object2ObjectOpenHashMap<EnumFacing, IClayLaser>()

    override fun hasCapability(capability: Capability<*>, facing: EnumFacing?): Boolean {
        return capability === ClayiumTileCapabilities.CLAY_LASER_SOURCE
                || capability === ClayiumTileCapabilities.CLAY_LASER_ACCEPTOR
                || super.hasCapability(capability, facing)
    }

    override fun <T> getCapability(capability: Capability<T>, facing: EnumFacing?): T? {
        return when {
            capability === ClayiumTileCapabilities.CLAY_LASER_SOURCE -> {
                ClayiumTileCapabilities.CLAY_LASER_SOURCE.cast(this)
            }
            capability === ClayiumTileCapabilities.CLAY_LASER_ACCEPTOR -> {
                ClayiumTileCapabilities.CLAY_LASER_ACCEPTOR.cast(this)
            }
            else -> super.getCapability(capability, facing)
        }
    }

    override fun update() {
        if (world.isRemote) return
        val (laserLength, laserTarget) = updateLengthAndTarget(world, pos, laserTarget) {
            notifyWorld()
        }
        this.laserLength = laserLength
        this.laserTarget = laserTarget
    }

    override fun invalidate() {
        val laserDirection = this.laser.direction
        this.laserTarget.takeIfValid()
            ?.getCapability(ClayiumTileCapabilities.CLAY_LASER_ACCEPTOR, laserDirection.opposite)
            ?.laserChanged(laserDirection.opposite, null)
        super.invalidate()
    }

    //todo fix
    // 無限再帰する可能性のある実装
    // 置かれたときは置かれた方のtargetがnullだから無限再帰しないだけ
    // なので2つの向かい合うリフレクタにレーザを照射し、
    // その間にブロックを置いてレーザーを遮断すると無限再帰する。
    // +
    // そもそも向かい合って設置されたとき、ageが3までしか伸びない。
    // 毎tick laserChangedを呼ぶのがよさそう？
    override fun laserChanged(irradiatedSide: EnumFacing, laser: IClayLaser?) {
        if (world.getBlockState(pos).block !== ClayiumBlocks.LASER_REFLECTOR) return
        if (laser == null) {
            this.receivedLasers.remove(irradiatedSide)
        } else {
            this.receivedLasers[irradiatedSide] = laser
        }
        val state = this.world.getBlockState(this.pos)
        val mergedLaserDirection = state.getValue(BlockClayLaserReflector.FACING)
        this.laser = mergeLasers(this.receivedLasers.values, mergedLaserDirection)
        this.isActive = this.receivedLasers.isNotEmpty()
        laserTarget?.takeIfValid()
            ?.getCapability(ClayiumTileCapabilities.CLAY_LASER_ACCEPTOR, this.laser.direction.opposite)
            ?.laserChanged(this.laser.direction.opposite, this.laser)
        notifyWorld()
    }

    override fun getUpdateTag(): NBTTagCompound {
        val laserRgb = (this.laser.red shl 16) or (this.laser.green shl 8) or this.laser.blue
        @Suppress("UsePropertyAccessSyntax")
        return super.getUpdateTag().apply {
            setInteger("laserRgb", laserRgb)
            setInteger("laserDirection", laser.direction.index)
            setInteger("laserLength", laserLength)
            setBoolean("isActive", isActive)
        }
    }

    override fun handleUpdateTag(tag: NBTTagCompound) {
        super.readFromNBT(tag)
        readLaserData(tag)
    }

    override fun getUpdatePacket(): SPacketUpdateTileEntity {
        val laserRgb = (this.laser.red shl 16) or (this.laser.green shl 8) or this.laser.blue
        return SPacketUpdateTileEntity(
            this.pos, 0,
            NBTTagCompound().apply {
                setInteger("laserRgb", laserRgb)
                setInteger("laserDirection", laser.direction.index)
                setInteger("laserLength", laserLength)
                setBoolean("isActive", isActive)
            }
        )
    }

    override fun onDataPacket(net: NetworkManager, pkt: SPacketUpdateTileEntity) {
        readLaserData(pkt.nbtCompound)
    }

    private fun readLaserData(data: NBTTagCompound) {
        val laserRgb = data.getInteger("laserRgb")
        val red = laserRgb shr 16 and 0xFF
        val green = laserRgb shr 8 and 0xFF
        val blue = laserRgb and 0xFF
        val laserDirection = EnumFacing.byIndex(data.getInteger("laserDirection"))

        this.laser = ClayLaser(laserDirection, red, green, blue)
        this.laserLength = data.getInteger("laserLength")
        this.isActive = data.getBoolean("isActive")
    }

    private fun notifyWorld() {
        val state = world.getBlockState(pos)
        world.notifyBlockUpdate(pos, state, state, Constants.BlockFlags.SEND_TO_CLIENTS)
    }

    override fun getRenderBoundingBox(): AxisAlignedBB {
        return INFINITE_EXTENT_AABB
    }

    private companion object {
        const val MAX_LASER_AGE = 10

        private fun mergeLasers(lasers: Collection<IClayLaser>, direction: EnumFacing): ClayLaser {
            if (lasers.isEmpty()) return ClayLaser(direction, 0, 0, 0)
            val maxAge = lasers.maxOf { it.age }
            if (maxAge >= MAX_LASER_AGE) {
                return ClayLaser(
                    direction,
                    lasers.maxOf { it.red },
                    lasers.maxOf { it.green },
                    lasers.maxOf { it.blue },
                    maxAge,
                )
            } else {
                return ClayLaser(
                    direction,
                    lasers.sumOf { it.red },
                    lasers.sumOf { it.green },
                    lasers.sumOf { it.blue },
                    maxAge + 1,
                )
            }
        }
    }
}