package io.github.trcdevelopers.clayium.api.events

import io.github.trcdevelopers.clayium.api.metatileentity.registry.CMteManager
import net.minecraftforge.fml.common.eventhandler.Event

class ClayiumMteRegistryEvent(
    val mteManager: CMteManager,
) : Event()