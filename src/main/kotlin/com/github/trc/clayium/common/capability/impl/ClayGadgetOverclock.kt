package com.github.trc.clayium.common.capability.impl

import com.github.trc.clayium.api.capability.IItemGadget
import com.github.trc.clayium.api.util.clayiumId
import com.github.trc.clayium.common.ClayiumMod
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.util.ResourceLocation

class ClayGadgetOverclock(
    val delay: Int,
) : IItemGadget {
    override val category: ResourceLocation = clayiumId("overclock")

    override fun putInHolder(player: EntityPlayer) {}
    override fun removeFromHolder(player: EntityPlayer) {}

    override fun updateInventory(player: EntityPlayer, isRemote: Boolean) {
        if (isRemote) {
            ClayiumMod.proxy.overclockPlayer(this.delay)
        }
    }
}