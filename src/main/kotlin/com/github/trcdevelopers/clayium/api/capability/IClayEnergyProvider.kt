package com.github.trcdevelopers.clayium.api.capability

import com.github.trcdevelopers.clayium.common.clayenergy.ClayEnergy

fun interface IClayEnergyProvider {
    fun getClayEnergy(): ClayEnergy
}