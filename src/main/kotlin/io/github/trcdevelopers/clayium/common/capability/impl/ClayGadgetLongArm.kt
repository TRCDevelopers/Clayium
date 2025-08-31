package io.github.trcdevelopers.clayium.common.capability.impl

import com.google.common.collect.HashMultimap
import io.github.trcdevelopers.clayium.api.capability.IItemGadget
import io.github.trcdevelopers.clayium.api.util.CUtils
import io.github.trcdevelopers.clayium.api.util.clayiumId
import net.minecraft.entity.ai.attributes.AttributeModifier
import net.minecraft.entity.player.EntityPlayer
import net.minecraftforge.common.util.Constants.AttributeModifierOperation.ADD

class ClayGadgetLongArm(
    additionalReach: Double,
) : IItemGadget {
    override val category = clayiumId("long_arm")

    private val modifier = AttributeModifier(CUtils.cUuid, "ClayiumGadgetLongArm", additionalReach, ADD)
    private val map: HashMultimap<String, AttributeModifier> = HashMultimap.create<String, AttributeModifier>().apply {
        put(EntityPlayer.REACH_DISTANCE.name, modifier)
    }

    override fun putInHolder(player: EntityPlayer) {
        if (!player.world.isRemote) {
            player.attributeMap.applyAttributeModifiers(map)
        }
    }

    override fun removeFromHolder(player: EntityPlayer) {
        if (!player.world.isRemote) {
            player.attributeMap.removeAttributeModifiers(map)
        }
    }
}