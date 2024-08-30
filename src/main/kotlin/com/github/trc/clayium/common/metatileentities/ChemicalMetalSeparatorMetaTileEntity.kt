package com.github.trc.clayium.common.metatileentities

import com.cleanroommc.modularui.utils.Alignment
import com.cleanroommc.modularui.value.sync.GuiSyncManager
import com.cleanroommc.modularui.value.sync.SyncHandlers
import com.cleanroommc.modularui.widget.ParentWidget
import com.cleanroommc.modularui.widgets.ItemSlot
import com.cleanroommc.modularui.widgets.SlotGroupWidget
import com.cleanroommc.modularui.widgets.layout.Row
import com.github.trc.clayium.api.CValues
import com.github.trc.clayium.api.capability.impl.NotifiableItemStackHandler
import com.github.trc.clayium.api.capability.impl.RecipeLogicEnergy
import com.github.trc.clayium.api.metatileentity.WorkableMetaTileEntity
import com.github.trc.clayium.api.util.ITier
import com.github.trc.clayium.api.util.clayiumId
import com.github.trc.clayium.common.recipe.registry.CRecipes
import net.minecraft.util.ResourceLocation

class ChemicalMetalSeparatorMetaTileEntity(
    metaTileEntityId: ResourceLocation,
    tier: ITier,
) : WorkableMetaTileEntity(metaTileEntityId, tier, validInputModesLists[1], validOutputModesLists[1],
    "machine.${CValues.MOD_ID}.chemical_metal_separator", CRecipes.CHEMICAL_METAL_SEPARATOR) {

    override val faceTexture = clayiumId("blocks/chemical_metal_separator")

    override val exportItems = NotifiableItemStackHandler(this, 116, this, isExport = true)
    override val workable = RecipeLogicEnergy(this, recipeRegistry, clayEnergyHolder)

    override fun buildMainParentWidget(syncManager: GuiSyncManager): ParentWidget<*> {
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

        return super.buildMainParentWidget(syncManager)
            .child(slotsAndProgressBar)
            .child(clayEnergyHolder.createSlotWidget()
                .align(Alignment.BottomRight))
            .child(clayEnergyHolder.createCeTextWidget(syncManager)
                .bottom(12).left(0))
    }

    override fun createMetaTileEntity() = ChemicalMetalSeparatorMetaTileEntity(metaTileEntityId, tier)
}