package com.github.trc.clayium.api.capability

import com.github.trc.clayium.api.capability.impl.AbstractRecipeLogic
import com.github.trc.clayium.api.metatileentity.trait.AutoIoHandler
import com.github.trc.clayium.api.pan.IPanAdapter
import com.github.trc.clayium.api.pan.IPanCable
import com.github.trc.clayium.api.pan.IPanUser
import net.minecraftforge.common.capabilities.Capability
import net.minecraftforge.common.capabilities.CapabilityInject

object ClayiumTileCapabilities {
    @CapabilityInject(IClayEnergyHolder::class)
    lateinit var CLAY_ENERGY_HOLDER: Capability<IClayEnergyHolder>
    @CapabilityInject(AutoIoHandler::class)
    lateinit var AUTO_IO_HANDLER: Capability<AutoIoHandler>

    @CapabilityInject(IClayLaserSource::class)
    lateinit var CLAY_LASER_SOURCE: Capability<IClayLaserSource>

    @CapabilityInject(IClayLaserAcceptor::class)
    lateinit var CLAY_LASER_ACCEPTOR: Capability<IClayLaserAcceptor>

    @CapabilityInject(IControllable::class)
    lateinit var CONTROLLABLE: Capability<IControllable>

    @CapabilityInject(AbstractWorkable::class)
    lateinit var WORKABLE: Capability<AbstractWorkable>
    @CapabilityInject(AbstractRecipeLogic::class)
    lateinit var RECIPE_LOGIC: Capability<AbstractRecipeLogic>

    @CapabilityInject(IPanCable::class)
    lateinit var PAN_CABLE: Capability<IPanCable>
    @CapabilityInject(IPanAdapter::class)
    lateinit var PAN_ADAPTER: Capability<IPanAdapter>
    @CapabilityInject(IPanUser::class)
    lateinit var PAN_USER: Capability<IPanUser>

    @CapabilityInject(IPipeConnectable::class)
    lateinit var PIPE_CONNECTABLE: Capability<IPipeConnectable>
}