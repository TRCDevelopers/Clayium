package com.github.trcdevelopers.clayium.api.capability

import com.github.trcdevelopers.clayium.common.clayenergy.ClayEnergy
import net.minecraftforge.items.IItemHandler

interface IClayEnergyHolder {

    val energizedClayItemHandler: IItemHandler

    fun getEnergyStored(): ClayEnergy

    /**
     * @return true if energy can/was drained, otherwise false
     */
    fun drawEnergy(ce: ClayEnergy, simulate: Boolean = false): Boolean

    fun hasEnoughEnergy(ce: ClayEnergy): Boolean
}