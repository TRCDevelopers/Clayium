package com.github.trcdevelopers.clayium.api.capability

import com.github.trcdevelopers.clayium.common.clayenergy.ClayEnergy

interface IClayEnergyHolder {
    fun getEnergyStored(): ClayEnergy

    /**
     * @return true if energy can/was drained, otherwise false
     */
    fun drawEnergy(ce: ClayEnergy, simulate: Boolean = false): Boolean

    fun hasEnoughEnergy(ce: ClayEnergy): Boolean
}