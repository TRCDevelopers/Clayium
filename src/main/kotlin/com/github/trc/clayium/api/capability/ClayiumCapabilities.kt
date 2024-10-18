package com.github.trc.clayium.api.capability

import net.minecraftforge.common.capabilities.Capability
import net.minecraftforge.common.capabilities.CapabilityInject

object ClayiumCapabilities {
    @CapabilityInject(ISynchronizedInterface::class)
    lateinit var SYNCHRONIZED_INTERFACE: Capability<ISynchronizedInterface>

    @CapabilityInject(IItemFilter::class) lateinit var ITEM_FILTER: Capability<IItemFilter>

    @CapabilityInject(IClayEnergyProvider::class)
    lateinit var ENERGIZED_CLAY: Capability<IClayEnergyProvider>

    @CapabilityInject(IConfigurationTool::class)
    lateinit var CONFIG_TOOL: Capability<IConfigurationTool>
}
