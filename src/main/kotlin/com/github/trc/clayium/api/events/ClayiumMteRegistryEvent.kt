package com.github.trc.clayium.api.events

import com.github.trc.clayium.api.metatileentity.registry.CMteManager
import net.minecraftforge.fml.common.eventhandler.Event

class ClayiumMteRegistryEvent(
    val mteManager: CMteManager,
) : Event()