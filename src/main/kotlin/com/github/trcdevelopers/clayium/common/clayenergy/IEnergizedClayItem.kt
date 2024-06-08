package com.github.trcdevelopers.clayium.common.clayenergy

import com.github.trcdevelopers.clayium.api.item.ITieredItem
import net.minecraft.item.ItemStack

interface IEnergizedClayItem : ITieredItem {
    fun getClayEnergy(stack: ItemStack): ClayEnergy
}
