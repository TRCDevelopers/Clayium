package com.github.trc.clayium.api.capability

import com.github.trc.clayium.common.clayenergy.ClayEnergy

fun interface IClayEnergyProvider {
    fun getClayEnergy(): ClayEnergy
}