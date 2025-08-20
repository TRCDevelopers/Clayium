package io.github.trcdevelopers.clayium.common.metatileentities

import com.cleanroommc.modularui.screen.ModularPanel
import com.cleanroommc.modularui.screen.UISettings
import com.cleanroommc.modularui.value.sync.FluidSlotSyncHandler
import com.cleanroommc.modularui.value.sync.PanelSyncManager
import com.cleanroommc.modularui.widget.ParentWidget
import com.cleanroommc.modularui.widgets.SlotGroupWidget
import com.cleanroommc.modularui.widgets.layout.Flow
import com.cleanroommc.modularui.widgets.slot.FluidSlot
import io.github.trcdevelopers.clayium.api.GUI_DEFAULT_WIDTH
import io.github.trcdevelopers.clayium.api.capability.IPipeConnectionLogic
import io.github.trcdevelopers.clayium.api.capability.impl.ClayiumItemStackHandler
import io.github.trcdevelopers.clayium.api.gui.data.MetaTileEntityGuiData
import io.github.trcdevelopers.clayium.api.metatileentity.MetaTileEntity
import io.github.trcdevelopers.clayium.api.metatileentity.trait.AutoIoHandler
import io.github.trcdevelopers.clayium.api.util.ITier
import io.github.trcdevelopers.clayium.api.util.MachineIoMode
import io.github.trcdevelopers.clayium.common.capability.impl.ClayiumFluidTank
import io.github.trcdevelopers.clayium.integration.modularui.MuiSlots
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.EnumFacing
import net.minecraft.util.ResourceLocation
import net.minecraftforge.common.capabilities.Capability
import net.minecraftforge.fluids.capability.CapabilityFluidHandler

class FluidBufferMetaTileEntity(
    metaTileEntityId: ResourceLocation,
    tier: ITier,
) : MetaTileEntity(
    metaTileEntityId,
    tier,
    validInputModes = listOf(MachineIoMode.NONE, MachineIoMode.ALL, MachineIoMode.FLUID),
    validOutputModes = listOf(MachineIoMode.NONE, MachineIoMode.ALL, MachineIoMode.FLUID),
    name = "fluid_buffer",
) {
    override val pipeConnectionLogic = IPipeConnectionLogic.ItemPipe

    val inventoryRowSize = when (tier.numeric) {
        in 4..7 -> tier.numeric - 3
        8, -> 4
        in 9..13 -> 6
        else -> 1
    }
    val inventoryColumnSize = when (tier.numeric) {
        in 4..7 -> tier.numeric - 2
        in 8..13 -> 9
        else -> 1
    }

    override val itemInventory = ClayiumItemStackHandler(this, inventoryRowSize * inventoryColumnSize)
    override val importItems = itemInventory
    override val exportItems = itemInventory

    private val fluidHandler = ClayiumFluidTank(this, inventoryRowSize * inventoryColumnSize * 64 * 1000)

    override fun writeToNBT(data: NBTTagCompound) {
        super.writeToNBT(data)

        val fluidCompound = NBTTagCompound()
        fluidHandler.writeToNBT(fluidCompound)
        data.setTag("fluid_handler", fluidCompound)
    }

    override fun readFromNBT(data: NBTTagCompound) {
        super.readFromNBT(data)

        fluidHandler.readFromNBT(data.getCompoundTag("fluid_handler"))
    }

    override fun <T> getCapability(capability: Capability<T>, facing: EnumFacing?): T? {
        if (capability === CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY) {
            return capability.cast(this.fluidHandler)
        }
        return super.getCapability(capability, facing)
    }

    override fun buildUI(data: MetaTileEntityGuiData, syncManager: PanelSyncManager, settings: UISettings): ModularPanel {
        return ModularPanel.defaultPanel(translationKey, GUI_DEFAULT_WIDTH, 18 + inventoryRowSize * 18 + 94 + 2 + 18 + 6)
            .columnWithPlayerInv {
                child(buildMainParentWidget(syncManager))
            }
    }

    override fun buildMainParentWidget(syncManager: PanelSyncManager): ParentWidget<*> {
        syncManager.registerSlotGroup("fluid_buffer_inv", inventoryRowSize)
        val columnStr = "I".repeat(inventoryColumnSize)
        val matrixStr = (0..<inventoryRowSize).map { columnStr }
        return super.buildMainParentWidget(syncManager)
            .child(Flow.column().top(9 + 3)
                .child(SlotGroupWidget.builder()
                    .matrix(*matrixStr.toTypedArray())
                    .key('I') {
                        MuiSlots.itemSlotBuilder(itemInventory, it).slotGroup("buffer_inv").build()
                    }
                    .build())
                .child(FluidSlot().syncHandler(FluidSlotSyncHandler(this.fluidHandler))
                    .marginTop(3))
            )
    }

    override fun createMetaTileEntity(): MetaTileEntity {
        return FluidBufferMetaTileEntity(metaTileEntityId, tier)
    }
}

class AutoIoHandlerFluidBuffer(
    metaTileEntity: MetaTileEntity,
    isBuffer: Boolean = true,
) : AutoIoHandler.Combined(
    metaTileEntity,
    isBuffer = isBuffer,
) {

}