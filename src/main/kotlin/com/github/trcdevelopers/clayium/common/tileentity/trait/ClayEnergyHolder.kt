package com.github.trcdevelopers.clayium.common.tileentity.trait

import com.cleanroommc.modularui.api.widget.IWidget
import com.cleanroommc.modularui.value.sync.GuiSyncManager
import com.cleanroommc.modularui.value.sync.SyncHandlers
import com.cleanroommc.modularui.widgets.ItemSlot
import com.github.trcdevelopers.clayium.common.clayenergy.ClayEnergy
import com.github.trcdevelopers.clayium.common.clayenergy.IEnergizedClay
import com.github.trcdevelopers.clayium.common.tileentity.TileEntityMachine
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraftforge.items.ItemStackHandler

class ClayEnergyHolder(
    tile: TileEntityMachine,
    tier: Int,
) : TETrait(tile, tier) {
    private val slot = object : ItemStackHandler(1) {
        override fun isItemValid(slot: Int, stack: ItemStack): Boolean {
            return stack.item is IEnergizedClay
        }

        override fun getStackLimit(slot: Int, stack: ItemStack): Int {
            //todo: upgradable
            return 1
        }
    }

    private var clayEnergy = ClayEnergy.ZERO

    /***
     * @return true if energy was drawn
     */
    fun drawEnergy(energy: ClayEnergy): Boolean {
        if (hasEnoughEnergy(energy)) {
            this.clayEnergy -= energy
            return true
        }
        return false
    }

    /**
     * tries to consume energized clay from the slot if the energy is not enough
     */
    fun hasEnoughEnergy(energy: ClayEnergy): Boolean {
        if (this.clayEnergy < energy) {
            tryConsumeEnergizedClay()
        }
        return this.clayEnergy >= energy
    }

    fun getSlotWidget(): ItemSlot {
        return ItemSlot()
            .slot(SyncHandlers.itemSlot(slot, 0)
                .accessibility(false, false))
    }

    fun syncValues(syncManager: GuiSyncManager) {
        syncManager.syncValue("clayEnergy", 2, SyncHandlers.longNumber(
            { clayEnergy.energy },
            { clayEnergy = ClayEnergy(it) }
        ))
    }

    private fun tryConsumeEnergizedClay() {
        val stack = this.slot.getStackInSlot(0)
        if (stack.isEmpty) return
        val item = stack.item as? IEnergizedClay ?: return
        this.clayEnergy += item.getClayEnergy(stack)
        this.slot.extractItem(0, 1, false)
    }

    override fun writeToNBT(data: NBTTagCompound) {
        data.setTag("slot", slot.serializeNBT())
        data.setLong("energy", clayEnergy.energy)
    }

    override fun readFromNBT(data: NBTTagCompound) {
        slot.deserializeNBT(data.getCompoundTag("slot"))
        clayEnergy = ClayEnergy(data.getLong("energy"))
    }
}