package com.github.trc.clayium.common.capability.impl

import com.github.trc.clayium.api.capability.ClayiumPlayerData
import com.github.trc.clayium.api.capability.IItemGadget
import com.github.trc.clayium.api.util.CUtils
import com.github.trc.clayium.api.util.clayiumId
import com.github.trc.clayium.common.ClayiumMod
import com.google.common.collect.HashMultimap
import net.minecraft.entity.SharedMonsterAttributes
import net.minecraft.entity.ai.attributes.AttributeModifier
import net.minecraft.entity.player.EntityPlayer
import net.minecraftforge.common.util.Constants

class ClayGadgetFlight(
    speed: Double,
    val mode: Int,
) : IItemGadget {

    override val category = clayiumId("flight")

    private val modifier = AttributeModifier(CUtils.cUuid, "ClayiumGadgetHealth", speed, Constants.AttributeModifierOperation.ADD)
    private val map: HashMultimap<String, AttributeModifier> = HashMultimap.create<String, AttributeModifier>().apply {
        put(SharedMonsterAttributes.MAX_HEALTH.name, modifier)
    }

    override fun updateInventory(player: EntityPlayer, isRemote: Boolean) {
        if (isRemote) {
            ClayiumMod.proxy.updateFlightStatus(mode)
        }
    }

    override fun onLogin(player: EntityPlayer) {
        if (player is EntityPlayer) {
            val wasFlying = player.getCapability(ClayiumPlayerData.CAPABILITY, null)?.wasFlying ?: false
            player.capabilities.allowFlying = true
            player.capabilities.isFlying = wasFlying
            player.sendPlayerAbilities()
        }
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