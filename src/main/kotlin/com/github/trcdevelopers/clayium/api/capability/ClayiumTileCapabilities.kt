package com.github.trcdevelopers.clayium.api.capability

import net.minecraftforge.common.capabilities.Capability
import net.minecraftforge.common.capabilities.CapabilityInject

object ClayiumTileCapabilities {
    @CapabilityInject(IClayEnergyHolder::class)
    lateinit var CAPABILITY_CLAY_ENERGY_HOLDER: Capability<IClayEnergyHolder>
}