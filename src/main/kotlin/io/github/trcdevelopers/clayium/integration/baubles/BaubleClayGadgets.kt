package io.github.trcdevelopers.clayium.integration.baubles

import baubles.api.cap.BaublesCapabilities
import io.github.trcdevelopers.clayium.api.capability.ClayiumCapabilities
import io.github.trcdevelopers.clayium.api.capability.IItemGadget
import io.github.trcdevelopers.clayium.common.items.ItemClayGadgetHolder
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemStack
import net.minecraftforge.items.CapabilityItemHandler

object BaubleClayGadgets {
    fun getBaubleGadgets(list: MutableList<IItemGadget>, player: EntityPlayer) {
        val holder = searchGadgetHolderInBaubles(player)
        val holderItemHandler = holder.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null)
            ?: return
        for (j in 0..<holderItemHandler.slots) {
            val gadgetStack = holderItemHandler.getStackInSlot(j)
            val gadget = gadgetStack.getCapability(ClayiumCapabilities.CLAY_GADGET, null)
            if (gadget != null) list.add(gadget)
        }
    }

    /**
     * Searches for the first instance of ItemClayGadgetHolder in the player's baubles.
     * @return Empty ItemStack if not found. Not null.
     */
    fun searchGadgetHolderInBaubles(player: EntityPlayer): ItemStack {
        val baubleItems = player.getCapability(BaublesCapabilities.CAPABILITY_BAUBLES, null)
            ?: return ItemStack.EMPTY

        for (i in 0..<baubleItems.slots) {
            val stack = baubleItems.getStackInSlot(i)
            if (stack.item is ItemClayGadgetHolder) {
                return stack
            }
        }
        return ItemStack.EMPTY
    }
}