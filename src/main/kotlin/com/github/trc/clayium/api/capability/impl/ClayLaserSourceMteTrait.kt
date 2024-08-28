package com.github.trc.clayium.api.capability.impl

import com.github.trc.clayium.api.capability.ClayiumDataCodecs
import com.github.trc.clayium.api.capability.ClayiumDataCodecs.UPDATE_LASER_ACTIVATION
import com.github.trc.clayium.api.capability.ClayiumDataCodecs.UPDATE_LASER_LENGTH
import com.github.trc.clayium.api.capability.IClayLaserSource
import com.github.trc.clayium.api.laser.ClayLaser
import com.github.trc.clayium.api.laser.IClayLaser
import com.github.trc.clayium.api.metatileentity.MTETrait
import com.github.trc.clayium.api.metatileentity.MetaTileEntity
import net.minecraft.network.PacketBuffer

class ClayLaserSourceMteTrait(
    metaTileEntity: MetaTileEntity,
    laserRed: Int = 0,
    laserGreen: Int = 0,
    laserBlue: Int = 0,
) : MTETrait(metaTileEntity, ClayiumDataCodecs.LASER_CONTROLLER), IClayLaserSource {

    private val sampleLaser = ClayLaser(red = laserRed, green = laserGreen, blue = laserBlue)
    private val delegate = ClayLaserSource(metaTileEntity)
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

    override val irradiatingLaser: IClayLaser?
        get() = if (isIrradiating) sampleLaser else null

    override val direction get() = metaTileEntity.frontFacing

    override fun update() {
        if (metaTileEntity.isRemote) return
        if (isIrradiating) {
            length = delegate.irradiateLaser(metaTileEntity.frontFacing, sampleLaser)
        } else {
            delegate.stopIrradiation(metaTileEntity.frontFacing)
        }
    }

    override fun onRemoval() {
        delegate.stopIrradiation(metaTileEntity.frontFacing)
    }

    override fun receiveCustomData(discriminator: Int, buf: PacketBuffer) {
        when (discriminator) {
            UPDATE_LASER_LENGTH -> length = buf.readInt()
            UPDATE_LASER_ACTIVATION -> isIrradiating = buf.readBoolean()
        }
    }
}