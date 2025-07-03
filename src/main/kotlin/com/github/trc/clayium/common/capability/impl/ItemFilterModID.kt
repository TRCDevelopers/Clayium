package com.github.trc.clayium.common.capability.impl

import net.minecraft.item.ItemStack

class ItemFilterModID(
    modId: String = "",
) : StringItemFilterBase(modId) {
    override fun test(stack: ItemStack): Boolean {
        val modId = stack.item.registryName?.namespace ?: return false
        return regex.matches(modId)
    }
}