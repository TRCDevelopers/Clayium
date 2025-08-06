package io.github.trcdevelopers.clayium.common.metatileentities

import com.cleanroommc.modularui.api.drawable.IKey
import com.cleanroommc.modularui.screen.ModularPanel
import com.cleanroommc.modularui.utils.Alignment
import com.cleanroommc.modularui.value.sync.PanelSyncManager
import com.cleanroommc.modularui.widget.ParentWidget
import com.cleanroommc.modularui.widgets.SlotGroupWidget
import com.cleanroommc.modularui.widgets.layout.Row
import io.github.trcdevelopers.clayium.api.GUI_DEFAULT_HEIGHT
import io.github.trcdevelopers.clayium.api.GUI_DEFAULT_WIDTH
import io.github.trcdevelopers.clayium.api.capability.impl.NotifiableItemStackHandler
import io.github.trcdevelopers.clayium.api.capability.impl.RecipeLogicEnergy
import io.github.trcdevelopers.clayium.api.gui.data.MetaTileEntityGuiData
import io.github.trcdevelopers.clayium.api.metatileentity.MteRenderingConfig
import io.github.trcdevelopers.clayium.api.metatileentity.WorkableMetaTileEntity
import io.github.trcdevelopers.clayium.api.util.ITier
import io.github.trcdevelopers.clayium.api.util.asWidgetResizing
import io.github.trcdevelopers.clayium.api.util.clayiumId
import io.github.trcdevelopers.clayium.common.recipe.registry.CRecipes
import io.github.trcdevelopers.clayium.common.util.SidelessI18n
import io.github.trcdevelopers.clayium.integration.modularui.MuiSlots
import net.minecraft.util.ResourceLocation

class ChemicalMetalSeparatorMetaTileEntity(
    metaTileEntityId: ResourceLocation,
    tier: ITier,
) : WorkableMetaTileEntity(metaTileEntityId, tier, CRecipes.CHEMICAL_METAL_SEPARATOR) {

    override val exportItems = NotifiableItemStackHandler(this, 116, this, isExport = true)
    override val workable = RecipeLogicEnergy(this, recipeRegistry, clayEnergyHolder)

    override fun buildUI(data: MetaTileEntityGuiData, syncManager: PanelSyncManager): ModularPanel {
        val slotsAndProgressBar = Row()
            .widthRel(0.8f).height(18 * 4)
            .align(Alignment.Center)
            .child(MuiSlots.itemSlotBuilder(importItems, 0).singletonSlotGroup().buildLarge()
                .align(Alignment.CenterLeft))
            .child(workable.getProgressBar(syncManager)
                .align(Alignment.CenterLeft).marginLeft(26 + 4))
            .child(SlotGroupWidget.builder()
                .matrix(*(0..3).map { "IIII" }.toTypedArray())
                .key('I') {
                    MuiSlots.itemSlotBuilder(exportItems, it).takeOnly().build()
                }
                .build()
                .align(Alignment.CenterRight)
            )

        return ModularPanel.defaultPanel(translationKey, GUI_DEFAULT_WIDTH, GUI_DEFAULT_HEIGHT + 20)
            .columnWithPlayerInv {
                @Suppress("DuplicatedCode") // Output slots layout is different from super.buildMainParentWidget
                child(ParentWidget().widthRel(1f).expanded().marginBottom(2)
                    .child(IKey.str(asStackForm().displayName).asWidget()
                        .align(Alignment.TopLeft))
                    .child(IKey.lang("container.inventory").asWidget().align(Alignment.BottomLeft))
                    .child(IKey.dynamic {
                        // if empty string, a bug occurs.
                        if (overclock != 1.0) SidelessI18n.format("gui.clayium.overclock", overclock) else " "
                    }.asWidgetResizing().alignment(Alignment.CenterRight).align(Alignment.BottomRight))
                    .child(slotsAndProgressBar)
                    .child(clayEnergyHolder.createSlotWidget()
                        .align(Alignment.BottomRight))
                    .child(clayEnergyHolder.createCeTextWidget(syncManager)
                        .bottom(12).left(0))
                )
            }
    }

    override fun createMetaTileEntity() = ChemicalMetalSeparatorMetaTileEntity(metaTileEntityId, tier)

    override val renderingConfig by lazy {
        MteRenderingConfig.face(clayiumId("blocks/chemical_metal_separator"))
    }
}