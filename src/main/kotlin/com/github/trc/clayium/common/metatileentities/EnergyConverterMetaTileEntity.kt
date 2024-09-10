package com.github.trc.clayium.common.metatileentities

import com.cleanroommc.modularui.api.drawable.IKey
import com.cleanroommc.modularui.utils.Alignment
import com.cleanroommc.modularui.value.sync.GuiSyncManager
import com.cleanroommc.modularui.value.sync.SyncHandlers
import com.cleanroommc.modularui.widget.ParentWidget
import com.cleanroommc.modularui.widgets.layout.Column
import com.github.trc.clayium.api.ClayEnergy
import com.github.trc.clayium.api.capability.impl.ClayEnergyHolder
import com.github.trc.clayium.api.capability.impl.EmptyItemStackHandler
import com.github.trc.clayium.api.capability.impl.EnergyStorageExportOnly
import com.github.trc.clayium.api.capability.impl.EnergyStorageSerializable
import com.github.trc.clayium.api.metatileentity.MetaTileEntity
import com.github.trc.clayium.api.util.ITier
import com.github.trc.clayium.api.util.clayiumId
import com.github.trc.clayium.common.config.ConfigFeGen
import net.minecraft.client.resources.I18n
import net.minecraft.client.util.ITooltipFlag
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.EnumFacing
import net.minecraft.util.ResourceLocation
import net.minecraft.world.World
import net.minecraftforge.common.capabilities.Capability
import net.minecraftforge.energy.CapabilityEnergy
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly

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

    private val feStorage = EnergyStorageSerializable(ConfigFeGen.feStorageSize[tier.numeric - 4])
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
        syncManager.syncValue("feStorage", SyncHandlers.intNumber(feStorage::getEnergyStored, feStorage::setEnergy))
        return super.buildMainParentWidget(syncManager)
            .child(ceHolder.createSlotWidget()
                .align(Alignment.BottomRight))
            .child(ceHolder.createCeTextWidget(syncManager)
                .left(0).bottom(10))
            .child(Column().widthRel(1f).height(8 * 5).align(Alignment.Center)
                .child(IKey.dynamic { I18n.format("gui.clayium.energy_converter.storage", feStorage.energyStored, feStorage.maxEnergyStored) }
                    .asWidget().widthRel(1f))
                .child(IKey.dynamic { I18n.format("gui.clayium.energy_converter.rate", cePerTick.format(), fePerTick) }
                    .asWidget().widthRel(1f).margin(0, 3))
                .child(IKey.dynamic { I18n.format("gui.clayium.energy_converter.output", fePerTick) }
                    .asWidget().widthRel(1f))
            )
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

    @SideOnly(Side.CLIENT)
    override fun addInformation(stack: ItemStack, worldIn: World?, tooltip: MutableList<String>, flagIn: ITooltipFlag) {
        super.addInformation(stack, worldIn, tooltip, flagIn)
        tooltip.add(I18n.format("machine.clayium.energy_converter.tooltip.rate", cePerTick.format(), fePerTick))
        tooltip.add(I18n.format("machine.clayium.energy_converter.tooltip.output", fePerTick))
    }

    override fun writeToNBT(data: NBTTagCompound) {
        super.writeToNBT(data)
        data.setTag("feStorage", feStorage.serializeNBT())
    }

    override fun readFromNBT(data: NBTTagCompound) {
        super.readFromNBT(data)
        feStorage.deserializeNBT(data.getCompoundTag("feStorage"))
    }
}