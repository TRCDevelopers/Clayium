package com.github.trc.clayium.api.capability.impl

import com.github.trc.clayium.api.unification.OreDictUnifier
import com.github.trc.clayium.common.capability.impl.StringItemFilterBase
import net.minecraft.item.ItemStack

class OreDictionaryItemFilter(
    oreName: String = "",
) : StringItemFilterBase(oreName) {
    override fun test(stack: ItemStack): Boolean {
        return OreDictUnifier.getOreNames(stack).any { regex.matches(it) }
    }
}