package com.github.trc.clayium.api.capability

import com.github.trc.clayium.api.ClayEnergy

/**
 * Capability for Energized Clay items.
 */
fun interface IClayEnergyProvider {
    fun getClayEnergy(): ClayEnergy
}