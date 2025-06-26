package com.github.trc.clayium.common.capability.impl

import net.minecraft.item.ItemStack

open class ItemFilterDamageValue(
    damage: String = "",
) : StringItemFilterBase(damage) {

    override val regex = Regex("^$damage$")

    override fun test(stack: ItemStack): Boolean {
        val damage = stack.itemDamage.toString()
        return regex.matches(damage)
    }
}