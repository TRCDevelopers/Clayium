package com.github.trcdevelopers.clayium.api.capability

import net.minecraftforge.common.capabilities.Capability
import net.minecraftforge.common.capabilities.CapabilityInject

object ClayiumCapabilities {
    @CapabilityInject(ISynchronizedInterface::class)
    lateinit var SYNCHRONIZED_INTERFACE: Capability<ISynchronizedInterface>
}