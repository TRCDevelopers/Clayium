package com.github.trcdevelopers.clayium.api.metatileentity

import com.cleanroommc.modularui.api.drawable.IDrawable
import com.cleanroommc.modularui.api.drawable.IKey
import com.cleanroommc.modularui.factory.PosGuiData
import com.cleanroommc.modularui.screen.ModularPanel
import com.cleanroommc.modularui.utils.Alignment
import com.cleanroommc.modularui.value.sync.GuiSyncManager
import com.cleanroommc.modularui.value.sync.SyncHandlers
import com.cleanroommc.modularui.widget.Widget
import com.cleanroommc.modularui.widgets.ItemSlot
import com.cleanroommc.modularui.widgets.SlotGroupWidget
import com.cleanroommc.modularui.widgets.layout.Column
import com.cleanroommc.modularui.widgets.layout.Row
import com.github.trcdevelopers.clayium.api.CTranslation
import com.github.trcdevelopers.clayium.api.CValues
import com.github.trcdevelopers.clayium.api.capability.ClayiumTileCapabilities
import com.github.trcdevelopers.clayium.api.capability.impl.AbstractRecipeLogic
import com.github.trcdevelopers.clayium.api.capability.impl.ClayEnergyHolder
import com.github.trcdevelopers.clayium.api.capability.impl.ItemHandlerProxy
import com.github.trcdevelopers.clayium.api.capability.impl.NotifiableItemStackHandler
import com.github.trcdevelopers.clayium.api.util.ITier
import com.github.trcdevelopers.clayium.common.blocks.machine.MachineIoMode
import com.github.trcdevelopers.clayium.common.gui.ClayGuiTextures
import com.github.trcdevelopers.clayium.common.recipe.registry.RecipeRegistry
import com.github.trcdevelopers.clayium.common.util.UtilLocale
import net.minecraft.client.gui.GuiScreen
import net.minecraft.client.renderer.block.model.ModelResourceLocation
import net.minecraft.client.util.ITooltipFlag
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.util.EnumFacing
import net.minecraft.util.ResourceLocation
import net.minecraft.world.World
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
) : MetaTileEntity(metaTileEntityId, tier, validInputModes, validOutputModes, translationKey) {

    val inputSize = recipeRegistry.maxInputs
    val outputSize = recipeRegistry.maxOutputs

    override val importItems = NotifiableItemStackHandler(this, inputSize, this, false)
    override val exportItems = NotifiableItemStackHandler(this, outputSize, this, true)
    override val itemInventory = ItemHandlerProxy(importItems, exportItems)
    override val autoIoHandler = AutoIoHandler.Combined(this)

    val clayEnergyHolder = ClayEnergyHolder(this)
    abstract val workable: AbstractRecipeLogic

    @SideOnly(Side.CLIENT)
    override fun registerItemModel(item: Item, meta: Int) {
        ModelLoader.setCustomModelResourceLocation(item, meta, ModelResourceLocation("${recipeRegistry.category.modid}:${recipeRegistry.category.categoryName}", "tier=${tier.numeric}"))
    }

    override fun <T> getCapability(capability: Capability<T>, facing: EnumFacing?): T? {
        if (capability == ClayiumTileCapabilities.CAPABILITY_CLAY_ENERGY_HOLDER) return ClayiumTileCapabilities.CAPABILITY_CLAY_ENERGY_HOLDER.cast(clayEnergyHolder)
        return super.getCapability(capability, facing)
    }

    override fun clearMachineInventory(itemBuffer: MutableList<ItemStack>) {
        super.clearMachineInventory(itemBuffer)
        clearInventory(itemBuffer, clayEnergyHolder.energizedClayItemHandler)
    }

    override fun buildUI(data: PosGuiData, syncManager: GuiSyncManager): ModularPanel {
        val panel = ModularPanel.defaultPanel(this.metaTileEntityId.toString())

        // title
        panel.child(IKey.lang("machine.clayium.${recipeRegistry.category.categoryName}", IKey.lang(tier.prefixTranslationKey)).asWidget()
            .top(6)
            .left(6))

        val slotsAndProgressBar = Row()

            .align(Alignment.Center)
            .top(30)
            .child(workable.getProgressBar(syncManager))

        //todo cleanup?
        if (importItems.slots == 1) {
            slotsAndProgressBar.child(Widget()
                .size(26, 26).left(4)
                .background(ClayGuiTextures.LARGE_SLOT))
                .child(ItemSlot().left(8).top(4)
                    .slot(SyncHandlers.itemSlot(importItems, 0)
                        .singletonSlotGroup(2))
                    .background(IDrawable.EMPTY))
        } else if (importItems.slots == 2) {
            syncManager.registerSlotGroup("input_inv", 1)
            slotsAndProgressBar.child(
                SlotGroupWidget.builder()
                    .matrix("II")
                    .key('I') { index ->
                        ItemSlot().slot(
                            SyncHandlers.itemSlot(importItems, index)
                                .slotGroup("input_inv"))
                            .apply {
                                if (index == 0)
                                    background(ClayGuiTextures.IMPORT_1_SLOT)
                                else
                                    background(ClayGuiTextures.IMPORT_2_SLOT)
                            }}
                    .build()
                    .align(Alignment.CenterLeft)
            )
        }

        if (exportItems.slots == 1) {
            slotsAndProgressBar.child(Widget()
                .size(26, 26).right(4)
                .background(ClayGuiTextures.LARGE_SLOT))
                .child(ItemSlot().right(8).top(4)
                    .slot(SyncHandlers.itemSlot(exportItems, 0)
                        .accessibility(false, true)
                        .singletonSlotGroup(0))
                    .background(IDrawable.EMPTY))
        } else if (importItems.slots == 2) {
            syncManager.registerSlotGroup("output_inv", 1)
            slotsAndProgressBar.child(
                SlotGroupWidget.builder()
                    .matrix("II")
                    .key('I') { index ->
                        ItemSlot().slot(
                            SyncHandlers.itemSlot(exportItems, index)
                                .accessibility(false, true)
                                .slotGroup("output_inv"))
                            .apply {
                                if (index == 0)
                                    background(ClayGuiTextures.EXPORT_1_SLOT)
                                else
                                    background(ClayGuiTextures.EXPORT_2_SLOT)
                            }}
                    .build()
                    .align(Alignment.CenterRight)
            )
        }
        panel.child(createBaseUi(syncManager)
                .child(slotsAndProgressBar)
                .child(clayEnergyHolder.createSlotWidget()
                    .right(7).top(58)
                    .setEnabledIf { GuiScreen.isShiftKeyDown() }
                    .background(IDrawable.EMPTY))
                .child(clayEnergyHolder.createCeTextWidget(syncManager)
                    .widthRel(0.5f)
                    .pos(6, 60))
                .child(playerInventoryTitle()
                    .align(Alignment.BottomLeft).left(8)))

        return panel.bindPlayerInventory()
    }

    protected open fun createBaseUi(syncManager: GuiSyncManager): Column {
        return Column().widthRel(1f).height(166 - 86)
    }
}