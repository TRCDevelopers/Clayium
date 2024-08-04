package com.github.trc.clayium.api.metatileentity

import com.cleanroommc.modularui.api.drawable.IDrawable
import com.cleanroommc.modularui.api.drawable.IKey
import com.cleanroommc.modularui.api.widget.IWidget
import com.cleanroommc.modularui.factory.PosGuiData
import com.cleanroommc.modularui.screen.ModularPanel
import com.cleanroommc.modularui.utils.Alignment
import com.cleanroommc.modularui.value.sync.GuiSyncManager
import com.cleanroommc.modularui.value.sync.InteractionSyncHandler
import com.cleanroommc.modularui.value.sync.SyncHandlers
import com.cleanroommc.modularui.widget.ParentWidget
import com.cleanroommc.modularui.widget.Widget
import com.cleanroommc.modularui.widgets.ButtonWidget
import com.cleanroommc.modularui.widgets.ItemSlot
import com.cleanroommc.modularui.widgets.SlotGroupWidget
import com.cleanroommc.modularui.widgets.layout.Column
import com.cleanroommc.modularui.widgets.layout.Row
import com.github.trc.clayium.api.ClayEnergy
import com.github.trc.clayium.api.capability.ClayiumTileCapabilities
import com.github.trc.clayium.api.capability.impl.AbstractRecipeLogic
import com.github.trc.clayium.api.capability.impl.ClayEnergyHolder
import com.github.trc.clayium.api.capability.impl.ItemHandlerProxy
import com.github.trc.clayium.api.capability.impl.NotifiableItemStackHandler
import com.github.trc.clayium.api.util.ITier
import com.github.trc.clayium.api.util.MachineIoMode
import com.github.trc.clayium.common.gui.ClayGuiTextures
import com.github.trc.clayium.common.recipe.registry.RecipeRegistry
import net.minecraft.client.gui.GuiScreen
import net.minecraft.client.renderer.block.model.ModelResourceLocation
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.util.EnumFacing
import net.minecraft.util.ResourceLocation
import net.minecraftforge.client.model.ModelLoader
import net.minecraftforge.common.capabilities.Capability
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly

abstract class WorkableMetaTileEntity(
    metaTileEntityId: ResourceLocation,
    tier: ITier,
    validInputModes: List<MachineIoMode>,
    validOutputModes: List<MachineIoMode>,
    translationKey: String,
    val recipeRegistry: RecipeRegistry<*>,
    val inputSize: Int = recipeRegistry.maxInputs,
    val outputSize: Int = recipeRegistry.maxOutputs,
) : MetaTileEntity(metaTileEntityId, tier, validInputModes, validOutputModes, translationKey) {

    constructor(metaTileEntityId: ResourceLocation, tier: ITier, recipeRegistry: RecipeRegistry<*>)
            : this(metaTileEntityId, tier, validInputModesLists[recipeRegistry.maxInputs], validOutputModesLists[recipeRegistry.maxOutputs],
        "machine.${metaTileEntityId.namespace}.${recipeRegistry.category.categoryName}", recipeRegistry)

    override val importItems = NotifiableItemStackHandler(this, inputSize, this, false)
    override val exportItems = NotifiableItemStackHandler(this, outputSize, this, true)
    override val itemInventory = ItemHandlerProxy(importItems, exportItems)
    val autoIoHandler = AutoIoHandler.Combined(this)

    val clayEnergyHolder = ClayEnergyHolder(this)
    abstract val workable: AbstractRecipeLogic

    @SideOnly(Side.CLIENT)
    override fun registerItemModel(item: Item, meta: Int) {
        ModelLoader.setCustomModelResourceLocation(item, meta, ModelResourceLocation("${recipeRegistry.category.modid}:${recipeRegistry.category.categoryName}", "tier=${tier.numeric}"))
    }

    override fun <T> getCapability(capability: Capability<T>, facing: EnumFacing?): T? {
        return when {
            capability === ClayiumTileCapabilities.CAPABILITY_CLAY_ENERGY_HOLDER -> capability.cast(clayEnergyHolder)
            capability === ClayiumTileCapabilities.RECIPE_LOGIC -> capability.cast(workable)
            else -> super.getCapability(capability, facing)
        }
    }

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

    override fun buildUI(data: PosGuiData, syncManager: GuiSyncManager): ModularPanel {
        val slotsAndProgressBar = Row()
            .widthRel(0.7f).height(26)
            .align(Alignment.Center)
            .top(30)
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

        return ModularPanel.defaultPanel(this.metaTileEntityId.toString())
            .child(Column().margin(7).sizeRel(1f)
                .child(createBaseUi(syncManager)
                    .child(slotsAndProgressBar.align(Alignment.Center))
                    .childIf(this.tier.numeric < 3, ButtonWidget()
                        .size(16, 16).align(Alignment.BottomCenter)
                        .overlay(ClayGuiTextures.CE_BUTTON)
                        .hoverOverlay(ClayGuiTextures.CE_BUTTON_HOVERED)
                        .syncHandler(InteractionSyncHandler().setOnMousePressed { clayEnergyHolder.addEnergy(ClayEnergy(1)) })
                    )
                )
                .child(SlotGroupWidget.playerInventory(0))
            )
    }

    protected open fun createBaseUi(syncManager: GuiSyncManager): ParentWidget<*> {
        return ParentWidget().widthRel(1f).expanded().marginBottom(2)
            .child(IKey.lang("machine.clayium.${recipeRegistry.category.categoryName}", IKey.lang(tier.prefixTranslationKey)).asWidget()
                .align(Alignment.TopLeft))
            .child(IKey.lang("container.inventory").asWidget()
                .align(Alignment.BottomLeft))
            .child(clayEnergyHolder.createCeTextWidget(syncManager)
                .bottom(12).left(0).widthRel(0.5f))
            .child(clayEnergyHolder.createSlotWidget()
                .align(Alignment.BottomRight)
                .setEnabledIf { GuiScreen.isShiftKeyDown() }
                .background(IDrawable.EMPTY))
    }
}