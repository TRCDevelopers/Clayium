package io.github.trcdevelopers.clayium.common.capability.impl

import net.minecraft.item.ItemStack

class ItemFilterDisplayName(
    displayName: String = "",
) : StringItemFilterBase(displayName) {
    override fun test(stack: ItemStack): Boolean {
        return regex.matches(stack.displayName)
    }
}