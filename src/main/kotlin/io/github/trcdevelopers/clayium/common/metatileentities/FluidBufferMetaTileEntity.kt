package io.github.trcdevelopers.clayium.common.metatileentities

import com.cleanroommc.modularui.screen.ModularPanel
import com.cleanroommc.modularui.screen.UISettings
import com.cleanroommc.modularui.value.sync.PanelSyncManager
import com.cleanroommc.modularui.widget.ParentWidget
import com.cleanroommc.modularui.widgets.SlotGroupWidget
import com.cleanroommc.modularui.widgets.layout.Flow
import io.github.trcdevelopers.clayium.api.GUI_DEFAULT_WIDTH
import io.github.trcdevelopers.clayium.api.capability.IPipeConnectionLogic
import io.github.trcdevelopers.clayium.api.capability.impl.ClayFluidCapsuleBackedItemFluidHandler
import io.github.trcdevelopers.clayium.api.gui.data.MetaTileEntityGuiData
import io.github.trcdevelopers.clayium.api.metatileentity.MetaTileEntity
import io.github.trcdevelopers.clayium.api.metatileentity.trait.AutoIoHandler
import io.github.trcdevelopers.clayium.api.util.ITier
import io.github.trcdevelopers.clayium.api.util.MachineIoMode
import io.github.trcdevelopers.clayium.common.items.ItemFluidCapsule
import io.github.trcdevelopers.clayium.integration.modularui.MuiSlots
import net.minecraft.util.EnumFacing
import net.minecraft.util.ResourceLocation
import net.minecraftforge.common.capabilities.Capability
import net.minecraftforge.fluids.capability.CapabilityFluidHandler
import net.minecraftforge.items.CapabilityItemHandler

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
        8 -> 4
        in 9..13 -> 6
        else -> 1
    }
    val inventoryColumnSize = when (tier.numeric) {
        in 4..7 -> tier.numeric - 2
        in 8..13 -> 9
        else -> 1
    }

    override val itemInventory = ClayFluidCapsuleBackedItemFluidHandler(this, inventoryRowSize * inventoryColumnSize)
    override val importItems = itemInventory
    override val exportItems = itemInventory

    private val autoIoHandler = AutoIoHandlerFluidBuffer(this, isBuffer = true)

    override fun onPlacement() {
        super.onPlacement()
        this.setInput(this.frontFacing.opposite, MachineIoMode.FLUID)
    }

    override fun <T> getCapability(capability: Capability<T>, facing: EnumFacing?): T? {
        return when {
            capability === CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY ->
                capability.cast(this.itemInventory)
            capability === CapabilityItemHandler.ITEM_HANDLER_CAPABILITY ->
                capability.cast(createFilteredItemHandler(itemInventory, facing))
            else -> super.getCapability(capability, facing)
        }
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
                        MuiSlots.itemSlotBuilder(itemInventory, it).slotGroup("fluid_buffer_inv").build()
                    }
                    .build())
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

    override fun transferItems(amount: Int) {
        this.importFluid(this.remainTransferImport)
        this.exportFluid(this.remainTransferExport)
        super.transferItems(amount)
    }

    override fun isImporting(side: EnumFacing): Boolean {
        val mode = metaTileEntity.getInput(side)
        return mode != MachineIoMode.NONE && mode != MachineIoMode.FLUID
    }

    override fun isExporting(side: EnumFacing): Boolean {
        val mode = metaTileEntity.getOutput(side)
        return mode != MachineIoMode.NONE && mode != MachineIoMode.FLUID
    }

    private fun importFluid(amount: Int) {
        var maxDrain = amount * ItemFluidCapsule.MAX_CAPACITY
        for (side in EnumFacing.entries) {
            if (maxDrain <= 0) break
            if (metaTileEntity.getInput(side) != MachineIoMode.FLUID) {
                continue
            }
            val insertTo = this.metaTileEntity.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, side)
                ?: continue

            val fluidHandler = metaTileEntity.getNeighborTileEntity(side)
                ?.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, side.opposite)
                ?: continue
            val drained = fluidHandler.drain(maxDrain, false)
                ?: continue
            val inserted = insertTo.fill(drained, false)

            val fluidStack = fluidHandler.drain(inserted, true)
                ?: continue
            insertTo.fill(fluidStack, true)
            maxDrain -= fluidStack.amount
        }
    }

    private fun exportFluid(amount: Int) {
        var maxDrain = amount * ItemFluidCapsule.MAX_CAPACITY
        for (side in EnumFacing.entries) {
            if (maxDrain <= 0) break
            if (metaTileEntity.getOutput(side) != MachineIoMode.FLUID) {
                continue
            }
            val extractFrom = this.metaTileEntity.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, side)
                ?: continue

            val fluidHandler = metaTileEntity.getNeighborTileEntity(side)
                ?.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, side.opposite)
                ?: continue

            val drained = extractFrom.drain(maxDrain, false)
                ?: continue
            val filled = fluidHandler.fill(drained, false)
            if (filled <= 0) continue

            val actuallyDrained = extractFrom.drain(filled, true)
                ?: continue
            fluidHandler.fill(actuallyDrained, true)
            maxDrain -= actuallyDrained.amount
        }
    }
}