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
import io.github.trcdevelopers.clayium.api.util.copyWithSize
import io.github.trcdevelopers.clayium.common.items.ClayiumItems
import io.github.trcdevelopers.clayium.common.items.ItemFluidCapsule
import io.github.trcdevelopers.clayium.common.util.FluidStackUtils
import io.github.trcdevelopers.clayium.integration.modularui.MuiSlots
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap
import net.minecraft.client.util.ITooltipFlag
import net.minecraft.item.ItemStack
import net.minecraft.util.EnumFacing
import net.minecraft.util.ResourceLocation
import net.minecraft.world.World
import net.minecraftforge.common.capabilities.Capability
import net.minecraftforge.fluids.capability.CapabilityFluidHandler
import net.minecraftforge.items.CapabilityItemHandler
import net.minecraftforge.items.IItemHandler
import net.minecraftforge.items.ItemHandlerHelper

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

    override fun addInformation(stack: ItemStack, worldIn: World?, tooltip: MutableList<String>, flagIn: ITooltipFlag) {
        super.addInformation(stack, worldIn, tooltip, flagIn)
        tooltip.add("Beta: Behavior may chane")
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
        super.transferItems(amount)
        this.importFluid(this.remainTransferImport)
        this.exportFluid(this.remainTransferExport)
    }

    private fun importFluid(amount: Int) {
        var remainingImport = amount
        for (side in EnumFacing.entries) {
            if (metaTileEntity.getInput(side) != MachineIoMode.FLUID) {
                continue
            }
            val insertTo = this.getImportItems(side) ?: continue

            val fluidHandler = metaTileEntity.getNeighborTileEntity(side)
                ?.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, side.opposite)
                ?: continue
            val fluidStack = fluidHandler.drain(Int.MAX_VALUE, false)
                ?: continue
            val itemStacks = FluidStackUtils.toCapsules(fluidStack, remainingImport * ItemFluidCapsule.MAX_CAPACITY)
            val (insertedItemCount, insertedFluidCount) = insertCapsulesTo(
                capsules = itemStacks,
                to = insertTo,
                amount = remainingImport,
                simulate = false,
            )
            remainingImport -= insertedItemCount
            if (insertedFluidCount > 0) {
                fluidHandler.drain(insertedFluidCount, true)
            }
        }
    }

    private fun exportFluid(amount: Int) {
        var remainingExport = amount
        for (side in EnumFacing.entries) {
            if (metaTileEntity.getOutput(side) != MachineIoMode.FLUID) {
                continue
            }
            val extractFrom = this.getExportItems(side) ?: continue

            val fluidHandler = metaTileEntity.getNeighborTileEntity(side)
                ?.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, side.opposite)
                ?: continue
        }
    }

    /**
     * @returns amount of fluid inserted
     */
    private fun insertCapsulesTo(
        capsules: List<ItemStack>,
        to: IItemHandler,
        amount: Int,
        simulate: Boolean,
    ): IntArray {
        var remainingWork = amount
        var insertedFluidAmount = 0
        for (capsule in capsules) {
            if (remainingWork <= 0) break
            val count = capsule.count.coerceAtMost(remainingWork)
            val capsuleItem = capsule.item as? ItemFluidCapsule ?: continue
            val capsuleCapacity = capsuleToAmount.getInt(capsuleItem)
            if (capsuleCapacity == capsuleToAmount.defaultReturnValue()) continue

            val extracted = capsule.copyWithSize(count)
            val remain = ItemHandlerHelper.insertItem(to, extracted, simulate)
            if (remain.isEmpty) {
                remainingWork -= extracted.count
                insertedFluidAmount += extracted.count * capsuleCapacity
            } else {
                val inserted = extracted.count - remain.count
                remainingWork -= inserted
                insertedFluidAmount += inserted * capsuleCapacity
            }
        }
        return intArrayOf(amount - remainingWork, insertedFluidAmount)
    }

    companion object {
        private val capsuleItems = listOf(
            ClayiumItems.FLUID_CAPSULE_1000MB,
            ClayiumItems.FLUID_CAPSULE_125MB,
            ClayiumItems.FLUID_CAPSULE_25MB,
            ClayiumItems.FLUID_CAPSULE_5MB,
            ClayiumItems.FLUID_CAPSULE_1MB,
        )
        private val capsuleToAmount: Object2IntOpenHashMap<ItemFluidCapsule> = capsuleItems
            .associateWithTo(Object2IntOpenHashMap()) {
            it.capacity
        }
    }
}