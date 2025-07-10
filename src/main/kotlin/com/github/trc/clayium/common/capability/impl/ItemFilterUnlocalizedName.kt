package com.github.trc.clayium.common.capability.impl

import net.minecraft.item.ItemStack

class ItemFilterUnlocalizedName(
    unlocalizedName: String = "",
) : StringItemFilterBase(unlocalizedName) {
    override fun test(stack: ItemStack): Boolean {
        return regex.matches(stack.translationKey)
    }
}