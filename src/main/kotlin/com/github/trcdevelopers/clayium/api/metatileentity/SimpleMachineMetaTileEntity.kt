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
import com.cleanroommc.modularui.widgets.layout.Row
import com.github.trcdevelopers.clayium.api.capability.ClayiumDataCodecs
import com.github.trcdevelopers.clayium.api.capability.impl.RecipeLogicEnergy
import com.github.trcdevelopers.clayium.api.util.CUtils
import com.github.trcdevelopers.clayium.common.blocks.machine.MachineIoMode
import com.github.trcdevelopers.clayium.common.gui.ClayGuiTextures
import com.github.trcdevelopers.clayium.common.recipe.registry.RecipeRegistry
import com.github.trcdevelopers.clayium.common.util.UtilLocale
import net.minecraft.client.gui.GuiScreen
import net.minecraft.client.util.ITooltipFlag
import net.minecraft.entity.EntityLivingBase
import net.minecraft.item.ItemStack
import net.minecraft.util.EnumFacing
import net.minecraft.util.ResourceLocation
import net.minecraft.world.World
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly

class SimpleMachineMetaTileEntity(
    metaTileEntityId: ResourceLocation,
    tier: Int,
    validInputModes: List<MachineIoMode>,
    validOutputModes: List<MachineIoMode>,
    translationKey: String,
    faceTexture: ResourceLocation,
    recipeRegistry: RecipeRegistry<*>,
) : WorkableMetaTileEntity(metaTileEntityId, tier, validInputModes, validOutputModes, translationKey, faceTexture, recipeRegistry) {

    constructor(
        metaTileEntityId: ResourceLocation,
        tier: Int,
        recipeRegistry: RecipeRegistry<*>,
    ) : this(metaTileEntityId, tier,
        CUtils.getValidInputModes(recipeRegistry.maxInputs), CUtils.getValidOutputModes(recipeRegistry.maxOutputs),
        "machine.${metaTileEntityId.namespace}.${recipeRegistry.category.categoryName}", ResourceLocation(metaTileEntityId.namespace, "blocks/${recipeRegistry.category.categoryName}"),
        recipeRegistry)

    override val workable = RecipeLogicEnergy(this, recipeRegistry, clayEnergyHolder)

    override fun createMetaTileEntity(): MetaTileEntity {
        return SimpleMachineMetaTileEntity(metaTileEntityId, tier, validInputModes, validOutputModes, translationKey, faceTexture, recipeRegistry)
    }

    override fun changeIoModesOnPlacement(placer: EntityLivingBase) {
        this._inputModes[EnumFacing.UP.index] = MachineIoMode.ALL
        this.refreshNeighborConnection(EnumFacing.UP)
        this._outputModes[EnumFacing.DOWN.index] = MachineIoMode.ALL
        this.refreshNeighborConnection(EnumFacing.DOWN)
        val placerFacing = placer.horizontalFacing
        this._inputModes[placerFacing.index] = MachineIoMode.CE
        this.refreshNeighborConnection(placerFacing.opposite)
        super.changeIoModesOnPlacement(placer)
    }

    override fun buildUI(data: PosGuiData, syncManager: GuiSyncManager): ModularPanel {
        val panel = ModularPanel.defaultPanel(this.metaTileEntityId.toString())

        // title
        panel.child(IKey.lang("machine.clayium.${recipeRegistry.category.categoryName}", IKey.lang("${ClayiumDataCodecs.Translation.MACHINE_TIER}$tier")).asWidget()
            .top(6)
            .left(6))

        val slotsAndProgressBar = Row()
            .widthRel(0.7f).height(26)
            .align(Alignment.Center)
            .top(30)
            .child(workable.getProgressBar(syncManager))

        //todo cleanup?
        if (importItems.slots == 1) {
            slotsAndProgressBar.child(Widget()
                .size(26, 26)
                .background(ClayGuiTextures.LARGE_SLOT)
                .align(Alignment.CenterLeft).left(4))
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
                .size(26, 26)
                .background(ClayGuiTextures.LARGE_SLOT)
                .align(Alignment.CenterRight).right(4))
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
        panel.child(slotsAndProgressBar)
            .child(clayEnergyHolder.createSlotWidget()
                .right(7).top(58)
                .setEnabledIf { GuiScreen.isShiftKeyDown() }
                .background(IDrawable.EMPTY))
            .child(clayEnergyHolder.createCeTextWidget(syncManager, 0)
                .widthRel(0.5f)
                .pos(6, 60))

        return panel.bindPlayerInventory()
    }

    @SideOnly(Side.CLIENT)
    override fun addInformation(stack: ItemStack, worldIn: World?, tooltip: MutableList<String>, flagIn: ITooltipFlag) {
        super.addInformation(stack, worldIn, tooltip, flagIn)
        UtilLocale.formatTooltips(tooltip, "machine.clayium.${metaTileEntityId.path}.tooltip")
    }
}