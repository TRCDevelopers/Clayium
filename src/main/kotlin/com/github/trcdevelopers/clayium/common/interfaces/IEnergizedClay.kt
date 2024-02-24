package com.github.trcdevelopers.clayium.common.interfaces

import com.github.trcdevelopers.clayium.common.ClayEnergy
import net.minecraft.item.ItemStack

interface IEnergizedClay {
    fun getClayEnergy(stack: ItemStack): ClayEnergy
}
