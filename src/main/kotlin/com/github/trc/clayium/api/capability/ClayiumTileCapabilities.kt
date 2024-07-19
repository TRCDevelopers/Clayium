package com.github.trc.clayium.api.capability

import com.github.trc.clayium.api.capability.impl.AbstractRecipeLogic
import com.github.trc.clayium.api.pan.IPanAdapter
import com.github.trc.clayium.api.pan.IPanCable
import net.minecraftforge.common.capabilities.Capability
import net.minecraftforge.common.capabilities.CapabilityInject

object ClayiumTileCapabilities {
    @CapabilityInject(IClayEnergyHolder::class)
    lateinit var CAPABILITY_CLAY_ENERGY_HOLDER: Capability<IClayEnergyHolder>

    @CapabilityInject(IClayLaserSource::class)
    lateinit var CAPABILITY_CLAY_LASER: Capability<IClayLaserSource>

    @CapabilityInject(IClayLaserAcceptor::class)
    lateinit var CAPABILITY_CLAY_LASER_ACCEPTOR: Capability<IClayLaserAcceptor>

    @CapabilityInject(IControllable::class)
    lateinit var CONTROLLABLE: Capability<IControllable>

    @CapabilityInject(AbstractRecipeLogic::class)
    lateinit var RECIPE_LOGIC: Capability<AbstractRecipeLogic>

    @CapabilityInject(IPanCable::class)
    lateinit var PAN_CABLE: Capability<IPanCable>
    @CapabilityInject(IPanAdapter::class)
    lateinit var PAN_ADAPTER: Capability<IPanAdapter>
}