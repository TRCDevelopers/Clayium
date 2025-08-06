package io.github.trcdevelopers.clayium.api.capability

import io.github.trcdevelopers.clayium.api.ClayEnergy

/**
 * Capability for Energized Clay items.
 */
fun interface IClayEnergyProvider {
    fun getClayEnergy(): ClayEnergy
}