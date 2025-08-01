package com.github.trc.clayium.integration.baubles

import baubles.api.cap.BaublesCapabilities
import com.github.trc.clayium.api.capability.IItemGadget
import net.minecraft.entity.player.EntityPlayer

object BaubleClayGadgets {
    fun getBaubleGadgets(list: MutableList<IItemGadget>, player: EntityPlayer) {
        val handler = player.getCapability(BaublesCapabilities.CAPABILITY_BAUBLES, null)
            ?: return

        for (i in 0..<handler.slots) {
            val stack = handler.getStackInSlot(i)
            if (stack.isEmpty) continue

            val gadget = stack.getCapability(com.github.trc.clayium.api.capability.ClayiumCapabilities.CLAY_GADGET, null)
            if (gadget != null && gadget !in list) {
                list.add(gadget)
            }
        }
    }
}