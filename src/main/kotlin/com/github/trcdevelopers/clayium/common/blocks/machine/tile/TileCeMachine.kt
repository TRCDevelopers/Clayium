package com.github.trcdevelopers.clayium.common.blocks.machine.tile

import com.github.trcdevelopers.clayium.common.clayenergy.ClayEnergy
import com.github.trcdevelopers.clayium.common.clayenergy.IEnergizedClay
import com.github.trcdevelopers.clayium.common.util.NBTTypeUtils.hasLong
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraftforge.items.ItemStackHandler

abstract class TileCeMachine : TileMachine() {

    val ceSlot = object : ItemStackHandler(1) {
        override fun onContentsChanged(slot: Int) {
            markDirty()
        }

        override fun isItemValid(slot: Int, stack: ItemStack): Boolean {
            return stack.item is IEnergizedClay
        }

        override fun getSlotLimit(slot: Int): Int {
            //todo upgradable
            return 1
        }
    }

    var storedCe: ClayEnergy = ClayEnergy.of(0)
        protected set

    fun extractCeFrom() {
        val itemStack = ceSlot.getStackInSlot(0)
        val item = itemStack.item
        if (item !is IEnergizedClay) return

        storedCe += item.getClayEnergy(itemStack)
        ceSlot.extractItem(0, 1, false)
    }

    fun consumeCe(ce: ClayEnergy) {
        storedCe -= ce
    }

    override fun writeToNBT(compound: NBTTagCompound): NBTTagCompound {
        compound.setLong("stored_ce", storedCe.energy)
        return super.writeToNBT(compound)
    }

    override fun readFromNBT(compound: NBTTagCompound) {
        storedCe = if (compound.hasLong("stored_ce")) ClayEnergy(compound.getLong("stored_ce")) else ClayEnergy.of(0)
        super.readFromNBT(compound)
    }
}