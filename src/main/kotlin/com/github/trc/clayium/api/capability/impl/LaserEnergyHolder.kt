package com.github.trc.clayium.api.capability.impl

import com.cleanroommc.modularui.api.drawable.IKey
import com.cleanroommc.modularui.value.sync.GuiSyncManager
import com.cleanroommc.modularui.value.sync.SyncHandlers
import com.cleanroommc.modularui.widgets.TextWidget
import com.github.trc.clayium.api.LaserEnergy
import com.github.trc.clayium.api.capability.ClayiumDataCodecs
import com.github.trc.clayium.api.capability.ClayiumTileCapabilities
import com.github.trc.clayium.api.capability.IClayLaserAcceptor
import com.github.trc.clayium.api.laser.ClayLaser
import com.github.trc.clayium.api.metatileentity.MTETrait
import com.github.trc.clayium.api.metatileentity.MetaTileEntity
import com.github.trc.clayium.api.util.asWidgetResizing
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.EnumFacing
import net.minecraftforge.common.capabilities.Capability

class LaserEnergyHolder(
    metaTileEntity: MetaTileEntity,
) : MTETrait(metaTileEntity, ClayiumDataCodecs.ENERGY_HOLDER), IClayLaserAcceptor {

    var storedPower = LaserEnergy.ZERO
        private set
    private var receivedLasers: Array<ClayLaser?> = arrayOfNulls(6)

    override fun update() {
        if (metaTileEntity.isRemote) return
        for (i in EnumFacing.entries.indices) {
            val laser = receivedLasers[i]
            if (laser != null) {
                this.storedPower += LaserEnergy(laser.energy)
            }
        }
    }

    fun drawAll(): Boolean {
        this.storedPower = LaserEnergy.ZERO
        return true
    }

    fun drawEnergy(power: LaserEnergy, simulate: Boolean): Boolean {
        if (!hasEnoughPower(power)) return false
        if (!simulate) this.storedPower -= power
        return true
    }

    fun hasEnoughPower(power: LaserEnergy): Boolean {
        return this.storedPower >= power
    }

    override fun <T> getCapability(capability: Capability<T>, facing: EnumFacing?): T? {
        if (capability == ClayiumTileCapabilities.CLAY_LASER_ACCEPTOR) {
            return ClayiumTileCapabilities.CLAY_LASER_ACCEPTOR.cast(this)
        }
        return super.getCapability(capability, facing)
    }

    fun createLpTextWidget(syncManager: GuiSyncManager): TextWidget {
        syncManager.syncValue("laser_power", SyncHandlers.doubleNumber(
            { storedPower.energy },
            { storedPower = LaserEnergy(it) }
        ))

        return IKey.dynamic { "Laser : ${this.storedPower.format()}" }.asWidgetResizing()
    }

    override fun serializeNBT(): NBTTagCompound {
        return super.serializeNBT().apply {
            setDouble("storedPower", storedPower.energy)
        }
    }

    override fun deserializeNBT(data: NBTTagCompound) {
        super.deserializeNBT(data)
        storedPower = LaserEnergy(data.getDouble("laserPower"))
    }

    override fun laserChanged(irradiatedSide: EnumFacing, laser: ClayLaser?) {
        this.receivedLasers[irradiatedSide.index] = laser
    }

}