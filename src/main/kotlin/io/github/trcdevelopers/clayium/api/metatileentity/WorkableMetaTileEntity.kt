package io.github.trcdevelopers.clayium.api.metatileentity

import com.cleanroommc.modularui.utils.Alignment
import com.cleanroommc.modularui.value.sync.InteractionSyncHandler
import com.cleanroommc.modularui.value.sync.PanelSyncManager
import com.cleanroommc.modularui.widget.ParentWidget
import com.cleanroommc.modularui.widgets.ButtonWidget
import com.cleanroommc.modularui.widgets.SlotGroupWidget
import com.cleanroommc.modularui.widgets.layout.Flow
import io.github.trcdevelopers.clayium.api.ClayEnergy
import io.github.trcdevelopers.clayium.api.capability.impl.AbstractRecipeLogic
import io.github.trcdevelopers.clayium.api.capability.impl.ClayEnergyHolder
import io.github.trcdevelopers.clayium.api.capability.impl.ItemHandlerProxy
import io.github.trcdevelopers.clayium.api.capability.impl.NotifiableItemStackHandler
import io.github.trcdevelopers.clayium.api.metatileentity.trait.AutoIoHandler
import io.github.trcdevelopers.clayium.api.util.ITier
import io.github.trcdevelopers.clayium.api.util.MachineIoMode
import io.github.trcdevelopers.clayium.common.gui.ClayGuiTextures
import io.github.trcdevelopers.clayium.common.recipe.registry.RecipeRegistry
import io.github.trcdevelopers.clayium.integration.modularui.MuiSlots
import net.minecraft.item.ItemStack
import net.minecraft.util.EnumFacing
import net.minecraft.util.ResourceLocation

abstract class WorkableMetaTileEntity(
    metaTileEntityId: ResourceLocation,
    tier: ITier,
    validInputModes: List<MachineIoMode>,
    validOutputModes: List<MachineIoMode>,
    val recipeRegistry: RecipeRegistry<*>,
    val inputSize: Int = recipeRegistry.maxInputs,
    val outputSize: Int = recipeRegistry.maxOutputs,
) : MetaTileEntity(metaTileEntityId, tier, validInputModes, validOutputModes, recipeRegistry.category.categoryName) {

    constructor(metaTileEntityId: ResourceLocation, tier: ITier, recipeRegistry: RecipeRegistry<*>) : this(
        metaTileEntityId, tier, validInputModesLists[recipeRegistry.maxInputs],
        validOutputModesLists[recipeRegistry.maxOutputs], recipeRegistry)

    override val importItems = NotifiableItemStackHandler(this, inputSize, this, false)
    override val exportItems = NotifiableItemStackHandler(this, outputSize, this, true)
    override val itemInventory = ItemHandlerProxy(importItems, exportItems)
    val autoIoHandler = AutoIoHandler.Combined(this)

    val clayEnergyHolder = ClayEnergyHolder(this)
    abstract val workable: AbstractRecipeLogic

    override fun onPlacement() {
        this.setInput(EnumFacing.UP, MachineIoMode.ALL)
        this.setOutput(EnumFacing.DOWN, MachineIoMode.ALL)
        this.setInput(this.frontFacing.opposite, MachineIoMode.CE)
        super.onPlacement()
    }

    override fun itemsDroppedOnDestroy(itemBuffer: MutableList<ItemStack>) {
        super.itemsDroppedOnDestroy(itemBuffer)
        clearInventory(itemBuffer, clayEnergyHolder.energizedClayItemHandler)
    }

    override fun buildMainParentWidget(syncManager: PanelSyncManager): ParentWidget<*> {
        val slotsAndProgressBar = Flow.row()
            .widthRel(0.7f).height(26)
            .center()
            .child(workable.getProgressBar(syncManager).center())

        if (importItems.slots == 1) {
            slotsAndProgressBar.child(
                MuiSlots.itemSlotBuilder(importItems, 0).singletonSlotGroup().build().left(0).verticalCenter()
            )
        } else if (importItems.slots == 2) {
            syncManager.registerSlotGroup("input_inv", 1)
            slotsAndProgressBar.child(
                SlotGroupWidget.builder()
                    .matrix("II").key('I') { index ->
                        MuiSlots.itemSlotBuilder(importItems, index).slotGroup("input_inv").build()
                            .apply {
                                if (index == 0) background(ClayGuiTextures.IMPORT_1_SLOT) else background(ClayGuiTextures.IMPORT_2_SLOT)
                            }}
                    .build()
                    .left(0).verticalCenter())
        }
        if (exportItems.slots == 1) {
            slotsAndProgressBar.child(
                MuiSlots.itemSlotBuilder(exportItems, 0).singletonSlotGroup().takeOnly().buildLarge()
            .right(0).verticalCenter())
        } else if (exportItems.slots == 2) {
            syncManager.registerSlotGroup("output_inv", 1)
            slotsAndProgressBar.child(
                SlotGroupWidget.builder()
                    .matrix("II").key('I') { index ->
                        MuiSlots.itemSlotBuilder(exportItems, index)
                            .slotGroup("output_inv")
                            .takeOnly()
                            .build()
                            .apply {
                                if (index == 0) background(ClayGuiTextures.EXPORT_1_SLOT) else background(ClayGuiTextures.EXPORT_2_SLOT)
                            }
                    }
                    .build()
                    .right(0).verticalCenter()
            )
        }

        return super.buildMainParentWidget(syncManager)
            .child(slotsAndProgressBar.center())
            .child(clayEnergyHolder.createCeTextWidget(syncManager)
                .name("CE Text")
                .bottom(12).left(0))
            .child(clayEnergyHolder.createSlotWidget()
                .name("CE Slot")
                .right(0).bottom(0))
            .childIf(tier.numeric < 3) { ButtonWidget()
                .size(16, 16).horizontalCenter().bottom(0)
                .overlay(ClayGuiTextures.CE_BUTTON)
                .hoverOverlay(ClayGuiTextures.CE_BUTTON_HOVERED)
                .syncHandler(InteractionSyncHandler().setOnMousePressed {
                    clayEnergyHolder.addEnergy(ClayEnergy(1))
                })}
    }
}