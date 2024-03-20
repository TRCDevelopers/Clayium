package com.github.trcdevelopers.clayium.common.clayenergy

import net.minecraft.item.ItemStack

interface IEnergizedClay {
    fun getClayEnergy(stack: ItemStack): ClayEnergy
}
