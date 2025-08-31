package io.github.trcdevelopers.clayium.api.capability.impl

import io.github.trcdevelopers.clayium.api.capability.ClayiumDataCodecs
import io.github.trcdevelopers.clayium.api.capability.ClayiumDataCodecs.UPDATE_LASER_ACTIVATION
import io.github.trcdevelopers.clayium.api.capability.ClayiumDataCodecs.UPDATE_LASER_LENGTH
import io.github.trcdevelopers.clayium.api.capability.ClayiumTileCapabilities
import io.github.trcdevelopers.clayium.api.capability.IClayLaserSource
import io.github.trcdevelopers.clayium.api.laser.ClayLaser
import io.github.trcdevelopers.clayium.api.metatileentity.MTETrait
import io.github.trcdevelopers.clayium.api.metatileentity.MetaTileEntity
import net.minecraft.network.PacketBuffer
import net.minecraft.util.EnumFacing
import net.minecraftforge.common.capabilities.Capability

class ClayLaserSourceMteTrait(
    metaTileEntity: MetaTileEntity,
    laserRed: Int = 0,
    laserGreen: Int = 0,
    laserBlue: Int = 0,
) : MTETrait(metaTileEntity, ClayiumDataCodecs.LASER_CONTROLLER), IClayLaserSource {

    val sampleLaser = ClayLaser(red = laserRed, green = laserGreen, blue = laserBlue)
    private val delegate = ClayLaserIrradiator(metaTileEntity)
    override var length = 0
        private set(value) {
            val syncFlag = !metaTileEntity.isRemote && (field != value)
            field = value
            if (syncFlag) {
                writeCustomData(UPDATE_LASER_LENGTH) {
                    writeInt(value)
                }
            }
        }
    var isIrradiating = false
        set(value) {
            val syncFlag = !metaTileEntity.isRemote && (field != value)
            field = value
            if (syncFlag) {
                writeCustomData(UPDATE_LASER_ACTIVATION) {
                    writeBoolean(value)
                }
            }
        }

    override val irradiatingLaser: ClayLaser?
        get() = if (isIrradiating) sampleLaser else null

    override val direction get() = metaTileEntity.frontFacing

    override fun update() {
        if (metaTileEntity.isRemote) return
        if (isIrradiating) {
            length = delegate.irradiateLaser(metaTileEntity.frontFacing, sampleLaser)
        } else {
            delegate.stopIrradiation()
        }
    }

    override fun onRemoval() {
        delegate.stopIrradiation()
    }

    override fun writeInitialSyncData(buf: PacketBuffer) {
        super.writeInitialSyncData(buf)
        buf.writeBoolean(isIrradiating)
        buf.writeVarInt(length)
    }

    override fun receiveInitialSyncData(buf: PacketBuffer) {
        super.receiveInitialSyncData(buf)
        isIrradiating = buf.readBoolean()
        length = buf.readVarInt()
    }

    override fun receiveCustomData(discriminator: Int, buf: PacketBuffer) {
        when (discriminator) {
            UPDATE_LASER_LENGTH -> length = buf.readInt()
            UPDATE_LASER_ACTIVATION -> isIrradiating = buf.readBoolean()
        }
    }

    override fun <T> getCapability(capability: Capability<T>, facing: EnumFacing?): T? {
        return if (capability === ClayiumTileCapabilities.CLAY_LASER_SOURCE) capability.cast(this) else null
    }
}