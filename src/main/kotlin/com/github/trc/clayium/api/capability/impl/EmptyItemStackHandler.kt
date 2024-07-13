package com.github.trc.clayium.api.capability.impl

import net.minecraftforge.items.IItemHandlerModifiable
import net.minecraftforge.items.wrapper.EmptyHandler

/**
 * forge's [EmptyHandler.INSTANCE] is declared as IItemHandler, so we need an IItemHandlerModifiable version
 */
object EmptyItemStackHandler : IItemHandlerModifiable by (EmptyHandler.INSTANCE as IItemHandlerModifiable)
