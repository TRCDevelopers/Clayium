package com.github.trc.clayium.common.capability.impl

import com.github.trc.clayium.api.capability.IItemGadget
import com.github.trc.clayium.api.util.CUtils
import com.google.common.collect.HashMultimap
import net.minecraft.entity.Entity
import net.minecraft.entity.EntityLivingBase
import net.minecraft.entity.SharedMonsterAttributes
import net.minecraft.entity.ai.attributes.AttributeModifier
import net.minecraft.item.ItemStack
import net.minecraftforge.common.util.Constants

class ClayGadgetHealth(
    val amount: Double,
) : IItemGadget {
    private val modifier = AttributeModifier(CUtils.cUuid, "ClayiumGadgetHealth", amount, Constants.AttributeModifierOperation.ADD)
    private val map: HashMultimap<String, AttributeModifier> = HashMultimap.create<String, AttributeModifier>().apply {
        put(SharedMonsterAttributes.MAX_HEALTH.name, modifier)
    }

    override fun putInHolder(player: Entity, gadgetStack: ItemStack) {
        if (player is EntityLivingBase) {
            player.attributeMap.applyAttributeModifiers(this.map)
        }
    }

    override fun removeFromHolder(player: Entity, gadgetStack: ItemStack) {
        if (player is EntityLivingBase) {
            player.attributeMap.removeAttributeModifiers(this.map)
        }
    }
}