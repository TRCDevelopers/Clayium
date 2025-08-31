package io.github.trcdevelopers.clayium.common.capability.impl

import net.minecraft.item.ItemStack

open class ItemFilterDamageValue(
    damage: String = "",
) : StringItemFilterBase(damage) {

    override fun createRegex(filter: String): Regex {
        return Regex("^$filter$")
    }

    override fun test(stack: ItemStack): Boolean {
        val damage = stack.itemDamage.toString()
        return regex.matches(damage)
    }
}