package com.github.trc.clayium.common.blocks.claycraftingtable

import com.cleanroommc.modularui.api.IGuiHolder
import com.cleanroommc.modularui.api.drawable.IDrawable
import com.cleanroommc.modularui.api.drawable.IKey
import com.cleanroommc.modularui.api.widget.IGuiAction
import com.cleanroommc.modularui.factory.PosGuiData
import com.cleanroommc.modularui.screen.ModularPanel
import com.cleanroommc.modularui.utils.Alignment
import com.cleanroommc.modularui.value.sync.GuiSyncManager
import com.cleanroommc.modularui.value.sync.SyncHandlers
import com.cleanroommc.modularui.widget.ParentWidget
import com.cleanroommc.modularui.widgets.ItemSlot
import com.cleanroommc.modularui.widgets.ProgressWidget
import com.cleanroommc.modularui.widgets.SlotGroupWidget
import com.cleanroommc.modularui.widgets.layout.Column
import com.cleanroommc.modularui.widgets.layout.Row
import com.github.trc.clayium.api.CValues
import com.github.trc.clayium.api.capability.impl.ClayiumItemStackHandler
import com.github.trc.clayium.api.metatileentity.interfaces.IMarkDirty
import com.github.trc.clayium.api.util.Mods
import com.github.trc.clayium.common.gui.ClayGuiTextures
import com.github.trc.clayium.integration.jei.JeiPlugin
import mezz.jei.api.recipe.VanillaRecipeCategoryUid
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.tileentity.TileEntity

class TileClayCraftingTable : TileEntity(), IMarkDirty, IGuiHolder<PosGuiData> {
    private val inputInventory = ClayiumItemStackHandler(this, 9)
    private val outputInventory = ClayiumItemStackHandler(this, 1)

    override fun writeToNBT(compound: NBTTagCompound): NBTTagCompound {
        val data = super.writeToNBT(compound)
        data.setTag("input_inventory", inputInventory.serializeNBT())
        data.setTag("output_inventory", outputInventory.serializeNBT())
        return data
    }

    override fun readFromNBT(compound: NBTTagCompound) {
        super.readFromNBT(compound)
        inputInventory.deserializeNBT(compound.getCompoundTag("input_inventory"))
        outputInventory.deserializeNBT(compound.getCompoundTag("output_inventory"))
    }

    override fun buildUI(data: PosGuiData, syncManager: GuiSyncManager): ModularPanel {
        syncManager.registerSlotGroup("input_inventory", 3)
        return ModularPanel.defaultPanel("clay_crafting_table")
            .child(Column().margin(7).sizeRel(1f)
                .child(ParentWidget().widthRel(1f).expanded().marginBottom(2)
                    .child(IKey.lang("tile.${CValues.MOD_ID}.clay_crafting_board.name").asWidget().align(Alignment.TopLeft))
                    .child(IKey.lang(CValues.INV_TRANS_KEY).asWidget().align(Alignment.BottomLeft))
                    .child(Row().widthRel(0.7f).height(18 * 3).align(Alignment.Center)
                        .child(SlotGroupWidget.builder()
                            .matrix("III", "III", "III")
                            .key('I') { i ->
                                ItemSlot().slot(SyncHandlers.itemSlot(inputInventory, i)
                                    .slotGroup("input_inventory"))
                            }.build().align(Alignment.CenterLeft)
                        )
                        .child(ProgressWidget().size(22, 17).progress { 0.0 }.texture(ClayGuiTextures.PROGRESS_BAR, 22)
                            .left(18 * 3 + 5).top(18 * 3 / 2 - 8)
                            .also {
                                if (Mods.JustEnoughItems.isModLoaded) {
                                    it.addTooltipLine(IKey.lang("jei.tooltip.show.recipes"))
                                        .listenGuiAction(IGuiAction.MousePressed { _ ->
                                            if (!it.isBelowMouse) return@MousePressed false
                                            JeiPlugin.jeiRuntime.recipesGui.showCategories(listOf(VanillaRecipeCategoryUid.CRAFTING))
                                            return@MousePressed true
                                        })
                                }
                            }
                        )
                        .child(ParentWidget().size(26, 26).background(ClayGuiTextures.LARGE_SLOT)
                            .child(ItemSlot().align(Alignment.Center)
                                .slot(SyncHandlers.itemSlot(outputInventory, 0))
                                .background(IDrawable.EMPTY))
                            .align(Alignment.CenterRight))
                    )
                )
                .child(SlotGroupWidget.playerInventory(0))
            )
    }
}