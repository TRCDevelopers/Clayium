package io.github.trcdevelopers.clayium.common.capability.impl

import com.google.common.collect.HashMultimap
import io.github.trcdevelopers.clayium.api.capability.IItemGadget
import io.github.trcdevelopers.clayium.api.util.CUtils
import io.github.trcdevelopers.clayium.api.util.clayiumId
import net.minecraft.entity.SharedMonsterAttributes
import net.minecraft.entity.ai.attributes.AttributeModifier
import net.minecraft.entity.player.EntityPlayer
import net.minecraftforge.common.util.Constants

class ClayGadgetHealth(
    val amount: Double,
) : IItemGadget {

    override val category = clayiumId("health")

    private val modifier = AttributeModifier(CUtils.cUuid, "ClayiumGadgetHealth", amount, Constants.AttributeModifierOperation.ADD)
    private val map: HashMultimap<String, AttributeModifier> = HashMultimap.create<String, AttributeModifier>().apply {
        put(SharedMonsterAttributes.MAX_HEALTH.name, modifier)
    }

    override fun putInHolder(player: EntityPlayer) {
        player.attributeMap.applyAttributeModifiers(this.map)
    }

    override fun removeFromHolder(player: EntityPlayer) {
        player.attributeMap.removeAttributeModifiers(this.map)
    }
}