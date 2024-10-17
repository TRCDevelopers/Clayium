package com.github.trc.clayium.api.item

import com.github.trc.clayium.api.util.ITier
import net.minecraft.item.ItemStack

interface ITieredItem {
    fun getTier(stack: ItemStack): ITier
}
