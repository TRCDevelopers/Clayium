package io.github.trcdevelopers.clayium.common.metatileentities

import com.cleanroommc.modularui.api.drawable.IKey
import com.cleanroommc.modularui.screen.ModularPanel
import com.cleanroommc.modularui.utils.Alignment
import com.cleanroommc.modularui.value.sync.PanelSyncManager
import com.cleanroommc.modularui.widget.ParentWidget
import com.cleanroommc.modularui.widgets.SlotGroupWidget
import com.cleanroommc.modularui.widgets.layout.Row
import io.github.trcdevelopers.clayium.api.GUI_DEFAULT_WIDTH
import io.github.trcdevelopers.clayium.api.capability.impl.RecipeLogicEnergy
import io.github.trcdevelopers.clayium.api.gui.data.MetaTileEntityGuiData
import io.github.trcdevelopers.clayium.api.metatileentity.MetaTileEntity
import io.github.trcdevelopers.clayium.api.metatileentity.MteRenderingConfig
import io.github.trcdevelopers.clayium.api.metatileentity.WorkableMetaTileEntity
import io.github.trcdevelopers.clayium.api.metatileentity.trait.AutoIoHandler
import io.github.trcdevelopers.clayium.api.util.ITier
import io.github.trcdevelopers.clayium.api.util.clayiumId
import io.github.trcdevelopers.clayium.common.config.ConfigTierBalance
import io.github.trcdevelopers.clayium.common.recipe.registry.CRecipes
import io.github.trcdevelopers.clayium.common.util.CNbtUtils
import io.github.trcdevelopers.clayium.common.util.SidelessI18n
import io.github.trcdevelopers.clayium.integration.modularui.MuiSlots
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.ResourceLocation
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World

class CentrifugeMetaTileEntity(
    metaTileEntityId: ResourceLocation,
    tier: ITier,
    outputSize: Int,
) : WorkableMetaTileEntity(metaTileEntityId, tier, validInputModesLists[1], validOutputModesLists[1], CRecipes.CENTRIFUGE,
    outputSize = outputSize) {

    @Suppress("Unused") private val ioHandler = AutoIoHandler.Combined(this)

    override val workable = RecipeLogicEnergy(this, recipeRegistry, clayEnergyHolder)
        .setDurationMultiplier(ConfigTierBalance.crafting::getCraftTimeMultiplier)
        .setEnergyConsumingMultiplier(ConfigTierBalance.crafting::getConsumingEnergyMultiplier)

    override fun buildUI(data: MetaTileEntityGuiData, syncManager: PanelSyncManager): ModularPanel {
        return ModularPanel.defaultPanel(this.metaTileEntityId.toString(), GUI_DEFAULT_WIDTH, 104 + ((outputSize + 1) * 9 + 46))
            .columnWithPlayerInv {
                child(buildMainParentWidget(syncManager))
            }
    }

    override fun buildMainParentWidget(syncManager: PanelSyncManager): ParentWidget<*> {
        val slotsAndProgressBar = Row()
            .widthRel(0.7f).height(26)
            .align(Alignment.Center)
            .top(30)
            .child(workable.getProgressBar(syncManager).align(Alignment.Center))

        slotsAndProgressBar.child(MuiSlots.itemSlotBuilder(importItems, 0).singletonSlotGroup().buildLarge()
            .align(Alignment.CenterLeft))
        slotsAndProgressBar.child(SlotGroupWidget.builder()
            .matrix(*(0..<outputSize).map { "I" }.toTypedArray())
            .key('I') {
                MuiSlots.itemSlotBuilder(exportItems, it).takeOnly().build()
            }
            .build()
            .align(Alignment.CenterRight)
        )

        @Suppress("DuplicatedCode") // special output slot layout
        return ParentWidget().widthRel(1f).expanded().marginBottom(2)
            .child(IKey.str(asStackForm().displayName).asWidget()
                .align(Alignment.TopLeft))
            .child(IKey.lang("container.inventory").asWidget().align(Alignment.BottomLeft))
            .child(IKey.dynamic {
                if (overclock != 1.0) SidelessI18n.format("gui.clayium.overclock", overclock) else " "
            }.asWidget().width(100).alignment(Alignment.CenterRight).align(Alignment.BottomRight))
            .child(slotsAndProgressBar.align(Alignment.Center))
            .child(clayEnergyHolder.createCeTextWidget(syncManager)
                .bottom(12).left(0))
            .child(clayEnergyHolder.createSlotWidget()
                .align(Alignment.BottomRight))
    }

    override fun onReplace(world: World, pos: BlockPos, newMetaTileEntity: MetaTileEntity, oldMteData: NBTTagCompound) {
        CNbtUtils.handleInvSizeDifference(world, pos, oldMteData, EXPORT_INVENTORY, newMetaTileEntity.exportItems)
    }

    override fun createMetaTileEntity(): MetaTileEntity {
        return CentrifugeMetaTileEntity(metaTileEntityId, tier, outputSize)
    }

    override val renderingConfig by lazy {
        MteRenderingConfig.face(clayiumId("blocks/centrifuge"))
    }
}