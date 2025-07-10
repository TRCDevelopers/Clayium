package com.github.trc.clayium.common.capability.impl

import com.github.trc.clayium.api.unification.OreDictUnifier
import net.minecraft.item.ItemStack

class ItemFilterOreDictionary(
    oreName: String = "",
) : StringItemFilterBase(oreName) {
    override fun test(stack: ItemStack): Boolean {
        return OreDictUnifier.getOreNames(stack).any { regex.matches(it) }
    }
}