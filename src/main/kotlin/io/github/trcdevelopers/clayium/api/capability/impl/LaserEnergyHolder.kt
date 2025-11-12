package io.github.trcdevelopers.clayium.api.capability.impl

import com.cleanroommc.modularui.api.drawable.IKey
import com.cleanroommc.modularui.utils.Alignment
import com.cleanroommc.modularui.value.sync.PanelSyncManager
import com.cleanroommc.modularui.value.sync.SyncHandlers
import com.cleanroommc.modularui.widgets.TextWidget
import io.github.trcdevelopers.clayium.api.LaserEnergy
import io.github.trcdevelopers.clayium.api.capability.ClayiumDataCodecs
import io.github.trcdevelopers.clayium.api.capability.ClayiumTileCapabilities
import io.github.trcdevelopers.clayium.api.capability.IClayLaserAcceptor
import io.github.trcdevelopers.clayium.api.laser.ClayLaser
import io.github.trcdevelopers.clayium.api.metatileentity.MTETrait
import io.github.trcdevelopers.clayium.api.metatileentity.MetaTileEntity
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.EnumFacing
import net.minecraftforge.common.capabilities.Capability

class LaserEnergyHolder(
    metaTileEntity: MetaTileEntity,
) : MTETrait(metaTileEntity, ClayiumDataCodecs.LASER_ENERGY_HOLDER), IClayLaserAcceptor {

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
        if (capability === ClayiumTileCapabilities.CLAY_LASER_ACCEPTOR) {
            return capability.cast(this)
        }
        return super.getCapability(capability, facing)
    }

    fun createLpTextWidget(syncManager: PanelSyncManager): TextWidget<*> {
        syncManager.syncValue("laser_power", SyncHandlers.doubleNumber(
            { storedPower.energy },
            { storedPower = LaserEnergy(it) }
        ))

        return IKey.dynamic { "Laser : ${this.storedPower.format()}" }.asWidget()
            .alignment(Alignment.Center)
            .width(60)
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

    override fun acceptLaser(irradiatedSide: EnumFacing, laser: ClayLaser?) {
        this.receivedLasers[irradiatedSide.index] = laser
    }

}