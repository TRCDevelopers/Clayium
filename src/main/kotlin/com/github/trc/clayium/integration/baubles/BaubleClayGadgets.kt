package com.github.trc.clayium.integration.baubles

import baubles.api.cap.BaublesCapabilities
import com.github.trc.clayium.api.capability.ClayiumCapabilities
import com.github.trc.clayium.api.capability.IItemGadget
import com.github.trc.clayium.common.items.ItemClayGadgetHolder
import net.minecraft.entity.player.EntityPlayer
import net.minecraftforge.items.CapabilityItemHandler

object BaubleClayGadgets {
    fun getBaubleGadgets(list: MutableList<IItemGadget>, player: EntityPlayer) {
        val baubleItems = player.getCapability(BaublesCapabilities.CAPABILITY_BAUBLES, null)
            ?: return

        for (i in 0..<baubleItems.slots) {
            val stack = baubleItems.getStackInSlot(i)
            if (stack.item !is ItemClayGadgetHolder) continue

            val handler = stack.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null)
                ?: continue
            for (j in 0..<handler.slots) {
                val gadgetStack = handler.getStackInSlot(j)
                val gadget = gadgetStack.getCapability(ClayiumCapabilities.CLAY_GADGET, null)
                if (gadget != null) {
                    list.add(gadget)
                }
            }
        }
    }
}