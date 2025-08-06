package io.github.trcdevelopers.clayium.common.capability.impl

import io.github.trcdevelopers.clayium.api.unification.OreDictUnifier
import net.minecraft.item.ItemStack

class ItemFilterOreDictionary(
    oreName: String = "",
) : StringItemFilterBase(oreName) {
    override fun test(stack: ItemStack): Boolean {
        return OreDictUnifier.getOreNames(stack).any { regex.matches(it) }
    }
}