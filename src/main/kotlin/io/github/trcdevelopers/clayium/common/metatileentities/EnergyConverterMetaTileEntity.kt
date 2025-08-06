package io.github.trcdevelopers.clayium.common.metatileentities

import com.cleanroommc.modularui.api.drawable.IKey
import com.cleanroommc.modularui.screen.ModularPanel
import com.cleanroommc.modularui.utils.Alignment
import com.cleanroommc.modularui.value.sync.PanelSyncManager
import com.cleanroommc.modularui.value.sync.SyncHandlers
import com.cleanroommc.modularui.widget.ParentWidget
import com.cleanroommc.modularui.widgets.layout.Column
import io.github.trcdevelopers.clayium.api.ClayEnergy
import io.github.trcdevelopers.clayium.api.GUI_DEFAULT_HEIGHT
import io.github.trcdevelopers.clayium.api.GUI_DEFAULT_WIDTH
import io.github.trcdevelopers.clayium.api.capability.impl.ClayEnergyHolder
import io.github.trcdevelopers.clayium.api.capability.impl.EmptyItemStackHandler
import io.github.trcdevelopers.clayium.api.capability.impl.EnergyStorageExportOnly
import io.github.trcdevelopers.clayium.api.capability.impl.EnergyStorageSerializable
import io.github.trcdevelopers.clayium.api.gui.data.MetaTileEntityGuiData
import io.github.trcdevelopers.clayium.api.metatileentity.MetaTileEntity
import io.github.trcdevelopers.clayium.api.metatileentity.MteRenderingConfig
import io.github.trcdevelopers.clayium.api.util.ITier
import io.github.trcdevelopers.clayium.api.util.MachineIoMode
import io.github.trcdevelopers.clayium.api.util.clayiumId
import io.github.trcdevelopers.clayium.common.config.ConfigCore
import io.github.trcdevelopers.clayium.common.util.SidelessI18n
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

    init {
        require(tier.numeric in 4..13) { "EnergyConverterMetaTileEntity can only be created with a tier between 4 and 13" }
    }

    override val importItems = EmptyItemStackHandler
    override val exportItems = EmptyItemStackHandler
    override val itemInventory = EmptyItemStackHandler
    private val clayEnergyHolder = ClayEnergyHolder(this)

    private val feStorage = EnergyStorageSerializable(ConfigCore.feGen.feStorageSize[tier.numeric - 4])
    private val exposedFeStorage = EnergyStorageExportOnly(feStorage)

    private val rawFePerTick = ConfigCore.feGen.fePerTick[tier.numeric - 4]
    private val fePerTick: Int get() = (rawFePerTick * overclock).toInt()
    private val rawCePerTick: ClayEnergy = ClayEnergy.of(1) * ConfigCore.feGen.cePerTick[tier.numeric - 4]
    private val cePerTick: ClayEnergy get() = (rawCePerTick * overclock)

    override fun update() {
        super.update()
        if (isRemote) return

        if (feStorage.receiveEnergy(fePerTick, true) == fePerTick
            && clayEnergyHolder.drawEnergy(cePerTick, false)) {
            feStorage.receiveEnergy(fePerTick, false)
        }
        //todo: control output allowed sides
        for (side in EnumFacing.entries) {
            val receiver = this.getNeighborTileEntity(side)?.getCapability(CapabilityEnergy.ENERGY, side.opposite)
            if (receiver != null && feStorage.energyStored > 0) {
                val maxTransfer = feStorage.extractEnergy(fePerTick, true)
                val actualTransfer = receiver.receiveEnergy(maxTransfer, false)
                feStorage.extractEnergy(actualTransfer, false)
            }
        }
    }

    override fun onPlacement() {
        this.setInput(this.frontFacing.opposite, MachineIoMode.CE)
        super.onPlacement()
    }

    override fun buildUI(data: MetaTileEntityGuiData, syncManager: PanelSyncManager): ModularPanel {
        return ModularPanel.defaultPanel(translationKey, GUI_DEFAULT_WIDTH, GUI_DEFAULT_HEIGHT + 10)
            .columnWithPlayerInv {
                child(buildMainParentWidget(syncManager))
            }
    }

    override fun buildMainParentWidget(syncManager: PanelSyncManager): ParentWidget<*> {
        syncManager.syncValue("feStorage", SyncHandlers.intNumber(feStorage::getEnergyStored, feStorage::setEnergy))
        return super.buildMainParentWidget(syncManager)
            .child(clayEnergyHolder.createSlotWidget()
                .align(Alignment.BottomRight))
            .child(clayEnergyHolder.createCeTextWidget(syncManager)
                .left(0).bottom(10))
            .child(Column().widthRel(1f).height(8 * 3 + 3 * 2 + 10).align(Alignment.Center)
                .child(IKey.dynamic { SidelessI18n.format("gui.clayium.energy_converter.storage", feStorage.energyStored, feStorage.maxEnergyStored) }
                    .asWidget().widthRel(1f))
                .child(IKey.dynamic { SidelessI18n.format("gui.clayium.energy_converter.rate", cePerTick.format(), fePerTick) }
                    .asWidget().widthRel(1f).margin(0, 3))
                .child(IKey.dynamic { SidelessI18n.format("gui.clayium.energy_converter.output", fePerTick) }
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

    override fun itemsDroppedOnDestroy(itemBuffer: MutableList<ItemStack>) {
        super.itemsDroppedOnDestroy(itemBuffer)
        clearInventory(itemBuffer, clayEnergyHolder.energizedClayItemHandler)
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

    override val renderingConfig by lazy {
        MteRenderingConfig.builder().face(clayiumId("blocks/energy_converter_overlay")).useFaceForAllSides().build()
    }
}