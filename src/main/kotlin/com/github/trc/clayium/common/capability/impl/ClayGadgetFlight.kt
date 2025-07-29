package com.github.trc.clayium.common.capability.impl

import com.github.trc.clayium.api.capability.IItemGadget
import com.github.trc.clayium.api.util.CUtils
import com.github.trc.clayium.api.util.clayiumId
import com.google.common.collect.HashMultimap
import net.minecraft.entity.Entity
import net.minecraft.entity.SharedMonsterAttributes
import net.minecraft.entity.ai.attributes.AttributeModifier
import net.minecraft.entity.player.EntityPlayer
import net.minecraftforge.common.util.Constants

class ClayGadgetFlight(
    speed: Double,
) : IItemGadget {

    override val category = clayiumId("flight")

    private val modifier = AttributeModifier(CUtils.cUuid, "ClayiumGadgetHealth", speed, Constants.AttributeModifierOperation.ADD)
    private val map: HashMultimap<String, AttributeModifier> = HashMultimap.create<String, AttributeModifier>().apply {
        put(SharedMonsterAttributes.MAX_HEALTH.name, modifier)
    }

    override fun putInHolder(player: Entity) {
        if (player is EntityPlayer) {
            if (player.isCreative || player.isSpectator) return
            player.capabilities.allowFlying = true
        }
    }

    override fun removeFromHolder(player: Entity) {
        if (player is EntityPlayer) {
            if (player.isCreative || player.isSpectator) return
            player.capabilities.allowFlying = false
            player.capabilities.isFlying = false
        }
    }
}