package com.github.trc.clayium.common.metatileentities

import com.cleanroommc.modularui.api.drawable.IKey
import com.cleanroommc.modularui.screen.ModularPanel
import com.cleanroommc.modularui.utils.Alignment
import com.cleanroommc.modularui.value.sync.GuiSyncManager
import com.cleanroommc.modularui.value.sync.SyncHandlers
import com.cleanroommc.modularui.widget.ParentWidget
import com.cleanroommc.modularui.widgets.ItemSlot
import com.cleanroommc.modularui.widgets.SlotGroupWidget
import com.cleanroommc.modularui.widgets.layout.Row
import com.github.trc.clayium.api.GUI_DEFAULT_WIDTH
import com.github.trc.clayium.api.capability.impl.RecipeLogicEnergy
import com.github.trc.clayium.api.gui.data.MetaTileEntityGuiData
import com.github.trc.clayium.api.metatileentity.MetaTileEntity
import com.github.trc.clayium.api.metatileentity.WorkableMetaTileEntity
import com.github.trc.clayium.api.metatileentity.trait.AutoIoHandler
import com.github.trc.clayium.api.util.ITier
import com.github.trc.clayium.api.util.asWidgetResizing
import com.github.trc.clayium.api.util.clayiumId
import com.github.trc.clayium.common.config.ConfigTierBalance
import com.github.trc.clayium.common.recipe.registry.CRecipes
import net.minecraft.client.resources.I18n
import net.minecraft.util.ResourceLocation

class CentrifugeMetaTileEntity(
    metaTileEntityId: ResourceLocation,
    tier: ITier,
    outputSize: Int,
) : WorkableMetaTileEntity(metaTileEntityId, tier, validInputModesLists[1], validOutputModesLists[1], CRecipes.CENTRIFUGE, outputSize = outputSize) {

    @Suppress("Unused") private val ioHandler = AutoIoHandler.Combined(this)

    override val faceTexture = clayiumId("blocks/centrifuge")
    override val workable = RecipeLogicEnergy(this, recipeRegistry, clayEnergyHolder)
        .setDurationMultiplier(ConfigTierBalance.crafting::getCraftTimeMultiplier)
        .setEnergyConsumingMultiplier(ConfigTierBalance.crafting::getConsumingEnergyMultiplier)

    override fun buildUI(data: MetaTileEntityGuiData, syncManager: GuiSyncManager): ModularPanel {
        return ModularPanel.defaultPanel(this.metaTileEntityId.toString(), GUI_DEFAULT_WIDTH, 104 + ((outputSize + 1) * 9 + 46))
            .columnWithPlayerInv {
                child(buildMainParentWidget(syncManager))
            }
    }

    override fun buildMainParentWidget(syncManager: GuiSyncManager): ParentWidget<*> {
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

        @Suppress("DuplicatedCode") // special output slot layout
        return ParentWidget().widthRel(1f).expanded().marginBottom(2)
            .child(IKey.str(getStackForm().displayName).asWidget()
                .align(Alignment.TopLeft))
            .child(IKey.lang("container.inventory").asWidget().align(Alignment.BottomLeft))
            .child(IKey.dynamic {
                if (overclock != 1.0) I18n.format("gui.clayium.overclock", overclock) else " "
            }.asWidgetResizing().alignment(Alignment.CenterRight).align(Alignment.BottomRight))
            .child(slotsAndProgressBar.align(Alignment.Center))
            .child(clayEnergyHolder.createCeTextWidget(syncManager)
                .bottom(12).left(0))
            .child(clayEnergyHolder.createSlotWidget()
                .align(Alignment.BottomRight))
    }

    override fun createMetaTileEntity(): MetaTileEntity {
        return CentrifugeMetaTileEntity(metaTileEntityId, tier, outputSize)
    }
}