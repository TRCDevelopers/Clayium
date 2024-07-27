package com.github.trc.clayium.api.capability.impl

import com.cleanroommc.modularui.api.drawable.IKey
import com.cleanroommc.modularui.value.sync.GuiSyncManager
import com.cleanroommc.modularui.value.sync.SyncHandlers
import com.cleanroommc.modularui.widgets.ItemSlot
import com.cleanroommc.modularui.widgets.TextWidget
import com.github.trc.clayium.api.capability.ClayiumCapabilities
import com.github.trc.clayium.api.capability.ClayiumDataCodecs
import com.github.trc.clayium.api.capability.IClayEnergyHolder
import com.github.trc.clayium.api.metatileentity.AutoIoHandler
import com.github.trc.clayium.api.metatileentity.MTETrait
import com.github.trc.clayium.api.metatileentity.MetaTileEntity
import com.github.trc.clayium.api.ClayEnergy
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound

class ClayEnergyHolder(
    metaTileEntity: MetaTileEntity,
) : MTETrait(metaTileEntity, ClayiumDataCodecs.ENERGY_HOLDER), IClayEnergyHolder {

    override val energizedClayItemHandler = object : ClayiumItemStackHandler(metaTileEntity, 1) {
        override fun isItemValid(slot: Int, stack: ItemStack): Boolean {
            return stack.hasCapability(ClayiumCapabilities.ENERGIZED_CLAY, null)
        }

        override fun getStackLimit(slot: Int, stack: ItemStack): Int {
            //todo: upgradable
            return 1
        }
    }

    private val energizedClayImporter = AutoIoHandler.EcImporter(metaTileEntity, energizedClayItemHandler)

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
            .slot(SyncHandlers.itemSlot(energizedClayItemHandler, 0)
                .accessibility(false, false))
    }

    fun createCeTextWidget(syncManager: GuiSyncManager): TextWidget {
        syncManager.syncValue("${this.name}.text", SyncHandlers.longNumber(
            { clayEnergy.energy },
            { clayEnergy = ClayEnergy(it) }
        ))

        return IKey.dynamic { this.clayEnergy.format() }.asWidget()
    }

    private fun tryConsumeEnergizedClay() {
        val stack = this.energizedClayItemHandler.getStackInSlot(0)
        if (stack.isEmpty) return
        val ceProvider = stack.getCapability(ClayiumCapabilities.ENERGIZED_CLAY, null) ?: return
        this.clayEnergy += ceProvider.getClayEnergy()
        this.energizedClayItemHandler.extractItem(0, 1, false)
    }

    override fun serializeNBT(): NBTTagCompound {
        return super.serializeNBT().apply {
            setLong("clayEnergy", clayEnergy.energy)
        }
    }

    override fun deserializeNBT(data: NBTTagCompound) {
        super.deserializeNBT(data)
        clayEnergy = ClayEnergy(data.getLong("clayEnergy"))
    }
}