package com.github.trcdevelopers.clayium.api.capability.impl

import com.github.trcdevelopers.clayium.api.capability.IClayEnergyProvider
import com.github.trcdevelopers.clayium.common.clayenergy.ClayEnergy

class SimpleClayEnergyProvider(val energy: ClayEnergy) : IClayEnergyProvider {
    override fun getClayEnergy() = energy
}