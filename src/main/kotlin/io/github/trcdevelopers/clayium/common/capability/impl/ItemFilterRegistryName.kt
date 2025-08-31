package io.github.trcdevelopers.clayium.common.capability.impl

import net.minecraft.item.ItemStack

class ItemFilterRegistryName(
    registryName: String = "",
) : StringItemFilterBase(registryName) {
    override fun test(stack: ItemStack): Boolean {
        val registryName = stack.item.registryName?.toString() ?: return false
        return regex.matches(registryName)
    }
}