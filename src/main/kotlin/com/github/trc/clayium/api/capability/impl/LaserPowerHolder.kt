package com.github.trc.clayium.api.capability.impl

import com.cleanroommc.modularui.api.drawable.IKey
import com.cleanroommc.modularui.value.sync.GuiSyncManager
import com.cleanroommc.modularui.value.sync.SyncHandlers
import com.cleanroommc.modularui.widgets.TextWidget
import com.github.trc.clayium.api.capability.ClayiumDataCodecs
import com.github.trc.clayium.api.capability.ClayiumTileCapabilities
import com.github.trc.clayium.api.capability.IClayLaserAcceptor
import com.github.trc.clayium.api.laser.IClayLaser
import com.github.trc.clayium.api.metatileentity.MTETrait
import com.github.trc.clayium.api.metatileentity.MetaTileEntity
import com.github.trc.clayium.common.clayenergy.LaserPower
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.EnumFacing
import net.minecraftforge.common.capabilities.Capability

class LaserPowerHolder(
    metaTileEntity: MetaTileEntity,
) : MTETrait(metaTileEntity, ClayiumDataCodecs.ENERGY_HOLDER), IClayLaserAcceptor {

    private var power: LaserPower = LaserPower.ZERO
    private var laser: Array<IClayLaser?> = arrayOfNulls(6)
    override fun update() {
        if (metaTileEntity.isRemote) return
        (0..5).map {
            if (this.laser[it] != null){
                this.power += LaserPower.of(this.laser[it]!!.laserEnergy)
            }
        }
    }

    fun getPowerStored(): LaserPower {
        return this.power
    }

    fun drawPower(lp: LaserPower, simulate: Boolean): Boolean {
        if (!hasEnoughPower(lp)) return false
        if (!simulate) this.power -= lp
        return true
    }
    fun hasEnoughPower(lp: LaserPower): Boolean {
        return this.power >= lp
    }

    override fun <T> getCapability(capability: Capability<T>, facing: EnumFacing?): T? {
        if (capability == ClayiumTileCapabilities.CAPABILITY_CLAY_LASER_ACCEPTOR) {
            return ClayiumTileCapabilities.CAPABILITY_CLAY_LASER_ACCEPTOR.cast(this);
        }
        return super.getCapability(capability, facing)
    }
    fun createLpTextWidget(syncManager: GuiSyncManager): TextWidget {
        syncManager.syncValue("${this.name}.text", SyncHandlers.doubleNumber(
            { power.lp },
            { power = LaserPower(it) }
        ))

        return IKey.dynamic { "Laser : ${this.power.format()}" }.asWidget()
    }

    override fun serializeNBT(): NBTTagCompound {
        return super.serializeNBT().apply {
            setDouble("laserPower", power.lp)
        }
    }

    override fun deserializeNBT(data: NBTTagCompound) {
        super.deserializeNBT(data)
        power = LaserPower(data.getDouble("laserPower"))
    }

    override fun laserChanged(
        irradiatedSide: EnumFacing,
        laser: IClayLaser?
    ) {
        this.laser[irradiatedSide.index] = laser
    }

}