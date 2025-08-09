package io.github.trcdevelopers.clayium.common.blocks.claycraftingtable

import com.cleanroommc.modularui.api.IGuiHolder
import com.cleanroommc.modularui.api.drawable.IKey
import com.cleanroommc.modularui.api.widget.IGuiAction
import com.cleanroommc.modularui.factory.PosGuiData
import com.cleanroommc.modularui.screen.ModularPanel
import com.cleanroommc.modularui.screen.UISettings
import com.cleanroommc.modularui.utils.Alignment
import com.cleanroommc.modularui.value.sync.PanelSyncManager
import com.cleanroommc.modularui.widget.ParentWidget
import com.cleanroommc.modularui.widgets.ProgressWidget
import com.cleanroommc.modularui.widgets.SlotGroupWidget
import com.cleanroommc.modularui.widgets.layout.Column
import com.cleanroommc.modularui.widgets.layout.Row
import io.github.trcdevelopers.clayium.api.MOD_ID
import io.github.trcdevelopers.clayium.api.capability.impl.ClayiumItemStackHandler
import io.github.trcdevelopers.clayium.api.metatileentity.interfaces.IMarkDirty
import io.github.trcdevelopers.clayium.api.util.CUtils
import io.github.trcdevelopers.clayium.api.util.Mods
import io.github.trcdevelopers.clayium.common.gui.ClayGuiTextures
import io.github.trcdevelopers.clayium.common.gui.ModularContainerClayCraftingBoard
import io.github.trcdevelopers.clayium.integration.jei.JeiPlugin
import io.github.trcdevelopers.clayium.integration.modularui.MuiSlots
import mezz.jei.api.recipe.VanillaRecipeCategoryUid
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.tileentity.TileEntity

class TileClayCraftingBoard : TileEntity(), IMarkDirty, IGuiHolder<PosGuiData> {
    val inventory = ClayiumItemStackHandler(this, 9)

    override fun writeToNBT(compound: NBTTagCompound): NBTTagCompound {
        val data = super.writeToNBT(compound)
        CUtils.writeItems(inventory, "inventory", data)
        return data
    }

    override fun readFromNBT(compound: NBTTagCompound) {
        super.readFromNBT(compound)
        CUtils.readItems(inventory, "inventory", compound)
    }

    override fun markAsDirty() = this.markDirty()

    override fun buildUI(data: PosGuiData, syncManager: PanelSyncManager, uiSettings: UISettings): ModularPanel {
        uiSettings.customContainer { ModularContainerClayCraftingBoard(3, 3, this.inventory) }

        syncManager.registerSlotGroup("input_inventory", 3)
        return ModularPanel.defaultPanel("clay_crafting_table")
            .child(Column().margin(7).sizeRel(1f)
                .child(ParentWidget().widthRel(1f).expanded().marginBottom(2)
                    .child(IKey.lang("tile.$MOD_ID.clay_crafting_board.name").asWidget().align(Alignment.TopLeft))
                    .child(IKey.lang("container.inventory").asWidget().align(Alignment.BottomLeft))
                    .child(Row().widthRel(0.7f).height(18 * 3).align(Alignment.Center)
                        .child(SlotGroupWidget.builder()
                            .matrix("III", "III", "III")
                            .key('I') { i ->
                                MuiSlots.itemSlotBuilder(inventory, i)
                                    .slotGroup("input_inventory").build()
                            }.build().align(Alignment.CenterLeft)
                        )
                        .child(ProgressWidget().size(22, 17).progress { 0.0 }.texture(ClayGuiTextures.PROGRESS_BAR, 22)
                            .left(18 * 3 + 5).top(18 * 3 / 2 - 8)
                            .also {
                                if (Mods.JustEnoughItems.isModLoaded) {
                                    it.addTooltipLine(IKey.lang("jei.tooltip.show.recipes"))
                                        .listenGuiAction(IGuiAction.MousePressed { _ ->
                                            if (!it.isBelowMouse) return@MousePressed false
                                            JeiPlugin.jeiRuntime.recipesGui.showCategories(listOf(
                                                VanillaRecipeCategoryUid.CRAFTING))
                                            return@MousePressed true
                                        })
                                }
                            }
                        )
//                        .child(MuiSlots.itemSlotBuilder(inventory, 9).craftingSlot().takeOnly().buildLarge()
//                            .align(Alignment.CenterRight))
                    )
                )
                .child(MuiSlots.playerInventory(0))
            )
    }
}