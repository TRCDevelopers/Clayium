package io.github.trcdevelopers.clayium.common.capability.impl

import io.github.trcdevelopers.clayium.api.capability.IItemGadget
import io.github.trcdevelopers.clayium.api.util.clayiumId
import io.github.trcdevelopers.clayium.common.ClayiumMod
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