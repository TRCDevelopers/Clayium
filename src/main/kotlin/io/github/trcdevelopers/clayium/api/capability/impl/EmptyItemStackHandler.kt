package io.github.trcdevelopers.clayium.api.capability.impl

import net.minecraft.item.ItemStack
import net.minecraftforge.items.IItemHandlerModifiable
import net.minecraftforge.items.wrapper.EmptyHandler

/**
 * forge's [EmptyHandler.INSTANCE] is declared as IItemHandler, so we need an IItemHandlerModifiable version
 */
object EmptyItemStackHandler : IItemHandlerModifiable by (EmptyHandler.INSTANCE as IItemHandlerModifiable) {
    override fun isItemValid(slot: Int, stack: ItemStack): Boolean {
        return EmptyHandler.INSTANCE.isItemValid(slot, stack)
    }
}
