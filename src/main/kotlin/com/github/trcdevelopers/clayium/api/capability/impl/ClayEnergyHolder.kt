package com.github.trcdevelopers.clayium.api.capability.impl

import com.cleanroommc.modularui.api.drawable.IKey
import com.cleanroommc.modularui.value.sync.GuiSyncManager
import com.cleanroommc.modularui.value.sync.SyncHandlers
import com.cleanroommc.modularui.widgets.ItemSlot
import com.cleanroommc.modularui.widgets.TextWidget
import com.github.trcdevelopers.clayium.api.CTranslation
import com.github.trcdevelopers.clayium.api.capability.ClayiumDataCodecs
import com.github.trcdevelopers.clayium.api.capability.IClayEnergyHolder
import com.github.trcdevelopers.clayium.api.metatileentity.AutoIoHandler
import com.github.trcdevelopers.clayium.api.metatileentity.MTETrait
import com.github.trcdevelopers.clayium.api.metatileentity.MetaTileEntity
import com.github.trcdevelopers.clayium.common.blocks.machine.MachineIoMode
import com.github.trcdevelopers.clayium.common.clayenergy.ClayEnergy
import com.github.trcdevelopers.clayium.common.clayenergy.IEnergizedClay
import net.minecraft.client.resources.I18n
import net.minecraft.item.ItemStack
import net.minecraft.util.EnumFacing
import net.minecraftforge.items.ItemStackHandler

class ClayEnergyHolder(
    metaTileEntity: MetaTileEntity,
) : MTETrait(metaTileEntity, ClayiumDataCodecs.ENERGY_HOLDER), IClayEnergyHolder {

    private val slot = object : ClayiumItemStackHandler(metaTileEntity, 1) {
        override fun isItemValid(slot: Int, stack: ItemStack): Boolean {
            return stack.item is IEnergizedClay
        }

        override fun getStackLimit(slot: Int, stack: ItemStack): Int {
            //todo: upgradable
            return 1
        }
    }

    private val energizedClayImporter = object : AutoIoHandler.Importer(metaTileEntity, slot, traitName = "${ClayiumDataCodecs.AUTO_IO_HANDLER}.${ClayiumDataCodecs.ENERGY_HOLDER}") {
        override fun isImporting(side: EnumFacing): Boolean {
            return metaTileEntity.getInput(side) == MachineIoMode.CE
        }
    }

    private var clayEnergy: ClayEnergy = ClayEnergy.ZERO

    override fun update() {
        energizedClayImporter.update()
    }

    override fun getEnergyStored(): ClayEnergy {
        return this.clayEnergy
    }

    override fun drawEnergy(ce: ClayEnergy, simulate: Boolean): Boolean {
        if (!hasEnoughEnergy(ce)) return false
        if (!simulate) this.clayEnergy -= ce
        return true
    }

    /**
     * tries to consume energized clay from the slot if the current energy is not enough
     */
    override fun hasEnoughEnergy(ce: ClayEnergy): Boolean {
        if (this.clayEnergy < ce) tryConsumeEnergizedClay()
        return this.clayEnergy >= ce
    }

    fun createSlotWidget(): ItemSlot {
        return ItemSlot()
            .slot(SyncHandlers.itemSlot(slot, 0)
                .accessibility(false, false))
    }

    fun createCeTextWidget(syncManager: GuiSyncManager, syncId: Int): TextWidget {
        syncManager.syncValue(this.name, syncId, SyncHandlers.longNumber(
            { clayEnergy.energy },
            { clayEnergy = ClayEnergy(it) }
        ))

        return IKey.dynamic {
            I18n.format(CTranslation.CLAY_ENERGY, this.clayEnergy.toString())
        }.asWidget()
    }

    private fun tryConsumeEnergizedClay() {
        val stack = this.slot.getStackInSlot(0)
        if (stack.isEmpty) return
        val item = stack.item as? IEnergizedClay ?: return
        this.clayEnergy += item.getClayEnergy(stack)
        this.slot.extractItem(0, 1, false)
    }
}