package io.github.trcdevelopers.clayium.common.capability.impl

import io.github.trcdevelopers.clayium.api.capability.ClayiumPlayerData
import io.github.trcdevelopers.clayium.api.capability.IItemGadget
import io.github.trcdevelopers.clayium.api.util.clayiumId
import io.github.trcdevelopers.clayium.common.ClayiumMod
import net.minecraft.entity.player.EntityPlayer

class ClayGadgetFlight(
    val mode: Int,
) : IItemGadget {

    override val category = clayiumId("flight")

    override fun updateInventory(player: EntityPlayer, isRemote: Boolean) {
        if (isRemote) {
            ClayiumMod.proxy.updateFlightStatus(mode)
        }
    }

    override fun onLogin(player: EntityPlayer) {
        val wasFlying = player.getCapability(ClayiumPlayerData.CAPABILITY, null)?.wasFlying ?: false
        player.capabilities.allowFlying = true
        player.capabilities.isFlying = wasFlying
        player.sendPlayerAbilities()
    }

    override fun onLogout(player: EntityPlayer) {
        player.getCapability(ClayiumPlayerData.CAPABILITY, null)?.let { data ->
            data.wasFlying = player.capabilities.isFlying
        }
    }

    override fun putInHolder(player: EntityPlayer) {
        if (player.isCreative || player.isSpectator) return
        player.capabilities.allowFlying = true

        player.sendPlayerAbilities()
    }

    override fun removeFromHolder(player: EntityPlayer) {
        if (player.isCreative || player.isSpectator) return
        player.capabilities.allowFlying = false
        player.capabilities.isFlying = false
        player.sendPlayerAbilities()
    }
}