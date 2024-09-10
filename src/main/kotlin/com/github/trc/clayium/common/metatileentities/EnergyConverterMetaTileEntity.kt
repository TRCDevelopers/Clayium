package com.github.trc.clayium.common.metatileentities

import com.cleanroommc.modularui.utils.Alignment
import com.cleanroommc.modularui.value.sync.GuiSyncManager
import com.cleanroommc.modularui.widget.ParentWidget
import com.github.trc.clayium.api.ClayEnergy
import com.github.trc.clayium.api.capability.impl.ClayEnergyHolder
import com.github.trc.clayium.api.capability.impl.EmptyItemStackHandler
import com.github.trc.clayium.api.capability.impl.EnergyStorageExportOnly
import com.github.trc.clayium.api.metatileentity.MetaTileEntity
import com.github.trc.clayium.api.util.ITier
import com.github.trc.clayium.api.util.clayiumId
import com.github.trc.clayium.common.config.ConfigFeGen
import net.minecraft.util.EnumFacing
import net.minecraft.util.ResourceLocation
import net.minecraftforge.common.capabilities.Capability
import net.minecraftforge.energy.CapabilityEnergy
import net.minecraftforge.energy.EnergyStorage

class EnergyConverterMetaTileEntity(
    metaTileEntityId: ResourceLocation,
    tier: ITier,
) : MetaTileEntity(metaTileEntityId, tier, energyAndNone, onlyNoneList, "energy_converter") {

    override val faceTexture = clayiumId("blocks/energy_converter_overlay")
    override val useFaceForAllSides: Boolean = true

    init {
        require(tier.numeric in 4..13) { "EnergyConverterMetaTileEntity can only be created with a tier between 4 and 13" }
    }

    override val importItems = EmptyItemStackHandler
    override val exportItems = EmptyItemStackHandler
    override val itemInventory = EmptyItemStackHandler
    private val ceHolder = ClayEnergyHolder(this)

    private val feStorage = EnergyStorage(ConfigFeGen.feStorageSize[tier.numeric - 4])
    private val fePerTick = ConfigFeGen.fePerTick[tier.numeric - 4]
    private val cePerTick: ClayEnergy = ClayEnergy.of(1) * ConfigFeGen.cePerTick[tier.numeric - 4]
    private val exposedFeStorage = EnergyStorageExportOnly(feStorage)

    override fun update() {
        super.update()
        if (isRemote) return
        if (ceHolder.drawEnergy(cePerTick, false)) {
            feStorage.receiveEnergy(fePerTick, false)
        }
        for (side in EnumFacing.entries) {
            val receiver = this.getNeighbor(side)?.getCapability(CapabilityEnergy.ENERGY, side.opposite)
            if (receiver != null && feStorage.energyStored > 0) {
                val maxTransfer = feStorage.extractEnergy(fePerTick, true)
                val actualTransfer = receiver.receiveEnergy(maxTransfer, false)
                feStorage.extractEnergy(actualTransfer, false)
            }
        }
    }

    override fun buildMainParentWidget(syncManager: GuiSyncManager): ParentWidget<*> {
        return super.buildMainParentWidget(syncManager)
            .child(ceHolder.createSlotWidget()
                .align(Alignment.BottomRight))
            .child(ceHolder.createCeTextWidget(syncManager)
                .left(0).bottom(10))
    }

    override fun createMetaTileEntity(): MetaTileEntity {
        return EnergyConverterMetaTileEntity(metaTileEntityId, tier)
    }

    override fun <T> getCapability(capability: Capability<T>, facing: EnumFacing?): T? {
        if (capability === CapabilityEnergy.ENERGY) {
            return capability.cast(exposedFeStorage)
        }
        return super.getCapability(capability, facing)
    }
}