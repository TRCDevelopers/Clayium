package com.github.trc.clayium.api.metatileentity

import com.cleanroommc.modularui.utils.Alignment
import com.cleanroommc.modularui.value.sync.GuiSyncManager
import com.cleanroommc.modularui.value.sync.InteractionSyncHandler
import com.cleanroommc.modularui.value.sync.SyncHandlers
import com.cleanroommc.modularui.widget.ParentWidget
import com.cleanroommc.modularui.widgets.ButtonWidget
import com.cleanroommc.modularui.widgets.ItemSlot
import com.cleanroommc.modularui.widgets.SlotGroupWidget
import com.cleanroommc.modularui.widgets.layout.Row
import com.github.trc.clayium.api.ClayEnergy
import com.github.trc.clayium.api.capability.impl.AbstractRecipeLogic
import com.github.trc.clayium.api.capability.impl.ClayEnergyHolder
import com.github.trc.clayium.api.capability.impl.ItemHandlerProxy
import com.github.trc.clayium.api.capability.impl.NotifiableItemStackHandler
import com.github.trc.clayium.api.metatileentity.trait.AutoIoHandler
import com.github.trc.clayium.api.util.ITier
import com.github.trc.clayium.api.util.MachineIoMode
import com.github.trc.clayium.common.gui.ClayGuiTextures
import com.github.trc.clayium.common.recipe.registry.RecipeRegistry
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

    constructor(metaTileEntityId: ResourceLocation, tier: ITier, recipeRegistry: RecipeRegistry<*>)
            : this(metaTileEntityId, tier, validInputModesLists[recipeRegistry.maxInputs], validOutputModesLists[recipeRegistry.maxOutputs], recipeRegistry)

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

    override fun clearMachineInventory(itemBuffer: MutableList<ItemStack>) {
        super.clearMachineInventory(itemBuffer)
        clearInventory(itemBuffer, clayEnergyHolder.energizedClayItemHandler)
    }

    override fun buildMainParentWidget(syncManager: GuiSyncManager): ParentWidget<*> {
        val slotsAndProgressBar = Row()
            .widthRel(0.7f).height(26)
            .align(Alignment.Center)
            .child(workable.getProgressBar(syncManager).align(Alignment.Center))

        if (importItems.slots == 1) {
            slotsAndProgressBar.child(largeSlot(SyncHandlers.itemSlot(importItems, 0).singletonSlotGroup())
                .align(Alignment.CenterLeft))
        } else if (importItems.slots == 2) {
            syncManager.registerSlotGroup("input_inv", 1)
            slotsAndProgressBar.child(
                SlotGroupWidget.builder()
                    .matrix("II").key('I') { index ->
                        ItemSlot().slot(
                            SyncHandlers.itemSlot(importItems, index)
                                .slotGroup("input_inv"))
                            .apply {
                                if (index == 0) background(ClayGuiTextures.IMPORT_1_SLOT) else background(ClayGuiTextures.IMPORT_2_SLOT)
                            }}
                    .build()
                    .align(Alignment.CenterLeft))
        }
        if (exportItems.slots == 1) {
            slotsAndProgressBar.child(largeSlot(SyncHandlers.itemSlot(exportItems, 0).singletonSlotGroup())
                .align(Alignment.CenterRight))
        } else if (exportItems.slots == 2) {
            syncManager.registerSlotGroup("output_inv", 1)
            slotsAndProgressBar.child(
                SlotGroupWidget.builder()
                    .matrix("II").key('I') { index ->
                        ItemSlot().slot(
                            SyncHandlers.itemSlot(exportItems, index)
                                .accessibility(false, true)
                                .slotGroup("output_inv"))
                            .apply {
                                if (index == 0) background(ClayGuiTextures.EXPORT_1_SLOT) else background(ClayGuiTextures.EXPORT_2_SLOT)
                            }}
                    .build()
                    .align(Alignment.CenterRight)
            )
        }

        return super.buildMainParentWidget(syncManager)
            .child(slotsAndProgressBar.align(Alignment.Center))
            .child(clayEnergyHolder.createCeTextWidget(syncManager)
                .bottom(12).left(0))
            .child(clayEnergyHolder.createSlotWidget()
                .align(Alignment.BottomRight))
            .childIf(tier.numeric < 3, ButtonWidget()
                .size(16, 16).align(Alignment.BottomCenter)
                .overlay(ClayGuiTextures.CE_BUTTON)
                .hoverOverlay(ClayGuiTextures.CE_BUTTON_HOVERED)
                .syncHandler(InteractionSyncHandler().setOnMousePressed {
                    clayEnergyHolder.addEnergy(ClayEnergy(1))
                }))
    }
}