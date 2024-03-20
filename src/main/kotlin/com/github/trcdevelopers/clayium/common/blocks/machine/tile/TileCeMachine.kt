package com.github.trcdevelopers.clayium.common.blocks.machine.tile

import com.github.trcdevelopers.clayium.common.clayenergy.ClayEnergy
import com.github.trcdevelopers.clayium.common.clayenergy.IEnergizedClay
import net.minecraft.item.ItemStack

abstract class TileCeMachine : TileMachine() {
    var storedCe: ClayEnergy = ClayEnergy.of(0)
        protected set

    fun extractCeFrom(itemStack: ItemStack) {
        val item = itemStack.item
        if (item !is IEnergizedClay) return

        storedCe += item.getClayEnergy(itemStack)
    }

    fun consumeCe(ce: ClayEnergy) {
        storedCe -= ce
    }
}