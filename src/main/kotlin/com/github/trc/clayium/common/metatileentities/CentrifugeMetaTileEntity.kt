package com.github.trc.clayium.common.metatileentities

import com.cleanroommc.modularui.factory.PosGuiData
import com.cleanroommc.modularui.screen.ModularPanel
import com.cleanroommc.modularui.utils.Alignment
import com.cleanroommc.modularui.value.sync.GuiSyncManager
import com.cleanroommc.modularui.value.sync.SyncHandlers
import com.cleanroommc.modularui.widgets.ItemSlot
import com.cleanroommc.modularui.widgets.SlotGroupWidget
import com.cleanroommc.modularui.widgets.layout.Column
import com.cleanroommc.modularui.widgets.layout.Row
import com.github.trc.clayium.api.CValues
import com.github.trc.clayium.api.capability.impl.RecipeLogicEnergy
import com.github.trc.clayium.api.metatileentity.MetaTileEntity
import com.github.trc.clayium.api.metatileentity.WorkableMetaTileEntity
import com.github.trc.clayium.api.util.ITier
import com.github.trc.clayium.api.util.clayiumId
import com.github.trc.clayium.common.recipe.registry.CRecipes
import net.minecraft.util.ResourceLocation

class CentrifugeMetaTileEntity(
    metaTileEntityId: ResourceLocation,
    tier: ITier,
    outputSize: Int,
) : WorkableMetaTileEntity(metaTileEntityId, tier, validInputModesLists[1], validOutputModesLists[1],
    "machine.${CValues.MOD_ID}.centrifuge", CRecipes.CENTRIFUGE, outputSize = outputSize) {
    override val faceTexture = clayiumId("blocks/centrifuge")
    override val workable = RecipeLogicEnergy(this, recipeRegistry, clayEnergyHolder)

    override fun buildUI(data: PosGuiData, syncManager: GuiSyncManager): ModularPanel {
        val slotsAndProgressBar = Row()
            .widthRel(0.7f).height(26)
            .align(Alignment.Center)
            .top(30)
            .child(workable.getProgressBar(syncManager).align(Alignment.Center))

        slotsAndProgressBar.child(largeSlot(SyncHandlers.itemSlot(importItems, 0).singletonSlotGroup())
            .align(Alignment.CenterLeft))
        slotsAndProgressBar.child(SlotGroupWidget.builder()
            .matrix(*(0..<outputSize).map { "I" }.toTypedArray())
            .key('I') {
                ItemSlot().slot(SyncHandlers.itemSlot(exportItems, it)
                    .accessibility(false, true))
            }
            .build()
            .align(Alignment.CenterRight)
        )

        return ModularPanel.defaultPanel(this.metaTileEntityId.toString(), 176, 104 + ((outputSize + 1) * 9 + 46))
            .child(Column().margin(7).sizeRel(1f)
                .child(buildMainParentWidget(syncManager)
                    .child(slotsAndProgressBar.align(Alignment.Center)))
                .child(SlotGroupWidget.playerInventory(0))
            )
    }

    override fun createMetaTileEntity(): MetaTileEntity {
        return CentrifugeMetaTileEntity(metaTileEntityId, tier, outputSize)
    }
}