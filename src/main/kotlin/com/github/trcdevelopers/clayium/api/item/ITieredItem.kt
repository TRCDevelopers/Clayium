package com.github.trcdevelopers.clayium.api.item

import com.github.trcdevelopers.clayium.api.util.ITier
import net.minecraft.item.ItemStack

interface ITieredItem {
    fun getTier(stack: ItemStack): ITier
}