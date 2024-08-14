package com.github.trc.clayium.common.metatileentities

import com.cleanroommc.modularui.api.drawable.IDrawable
import com.cleanroommc.modularui.api.drawable.IKey
import com.cleanroommc.modularui.factory.PosGuiData
import com.cleanroommc.modularui.screen.ModularPanel
import com.cleanroommc.modularui.utils.Alignment
import com.cleanroommc.modularui.value.sync.GuiSyncManager
import com.cleanroommc.modularui.value.sync.SyncHandlers
import com.cleanroommc.modularui.widget.ParentWidget
import com.cleanroommc.modularui.widgets.ItemSlot
import com.cleanroommc.modularui.widgets.SlotGroupWidget
import com.cleanroommc.modularui.widgets.layout.Column
import com.cleanroommc.modularui.widgets.layout.Row
import com.github.trc.clayium.api.CValues
import com.github.trc.clayium.api.capability.impl.NotifiableItemStackHandler
import com.github.trc.clayium.api.capability.impl.RecipeLogicEnergy
import com.github.trc.clayium.api.metatileentity.WorkableMetaTileEntity
import com.github.trc.clayium.api.util.ITier
import com.github.trc.clayium.api.util.clayiumId
import com.github.trc.clayium.common.recipe.registry.CRecipes
import net.minecraft.client.gui.GuiScreen
import net.minecraft.util.ResourceLocation

class ChemicalMetalSeparatorMetaTileEntity(
    metaTileEntityId: ResourceLocation,
    tier: ITier,
) : WorkableMetaTileEntity(metaTileEntityId, tier, validInputModesLists[1], validOutputModesLists[1],
    "machine.${CValues.MOD_ID}.chemical_metal_separator", CRecipes.CHEMICAL_METAL_SEPARATOR) {

    override val faceTexture = clayiumId("blocks/chemical_metal_separator")

    override val exportItems = NotifiableItemStackHandler(this, 116, this, isExport = true)
    override val workable = RecipeLogicEnergy(this, recipeRegistry, clayEnergyHolder)

    override fun buildUI(data: PosGuiData, syncManager: GuiSyncManager): ModularPanel {
        val slotsAndProgressBar = Row()
            .widthRel(0.8f).height(18 * 4)
            .align(Alignment.Center)
            .child(largeSlot(SyncHandlers.itemSlot(importItems, 0).singletonSlotGroup())
                .align(Alignment.CenterLeft))
            .child(workable.getProgressBar(syncManager)
                .align(Alignment.CenterLeft).marginLeft(26 + 4))
            .child(SlotGroupWidget.builder()
                .matrix(*(0..3).map { "IIII" }.toTypedArray())
                .key('I') {
                    ItemSlot().slot(SyncHandlers.itemSlot(exportItems, it)
                        .accessibility(false, true))
                }
                .build()
                .align(Alignment.CenterRight)
            )

        return ModularPanel.defaultPanel("chemical_metal_separator", 176, 190)
            .child(Column().margin(7).sizeRel(1f)
                .child(ParentWidget().widthRel(1f).expanded().marginBottom(2)
                    .child(IKey.lang(this.translationKey, IKey.lang(tier.prefixTranslationKey)).asWidget()
                        .align(Alignment.TopLeft))
                    .child(IKey.lang("container.inventory").asWidget()
                        .align(Alignment.BottomLeft))
                    .child(slotsAndProgressBar)
                    .child(clayEnergyHolder.createSlotWidget()
                        .align(Alignment.BottomRight)
                        .setEnabledIf { GuiScreen.isShiftKeyDown() }
                        .background(IDrawable.EMPTY))
                    .child(clayEnergyHolder.createCeTextWidget(syncManager).widthRel(0.5f)
                        .bottom(12).left(0))
                )
                .child(SlotGroupWidget.playerInventory(0))
            )
    }

    override fun createMetaTileEntity() = ChemicalMetalSeparatorMetaTileEntity(metaTileEntityId, tier)
}