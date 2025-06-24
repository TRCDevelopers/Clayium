package com.github.trc.clayium.api.events

import com.github.trc.clayium.api.item.filter.ItemFilterRegistry
import net.minecraftforge.fml.common.eventhandler.Event

class ClayiumFilterRegistrationEvent(
    val registry: ItemFilterRegistry,
) : Event()