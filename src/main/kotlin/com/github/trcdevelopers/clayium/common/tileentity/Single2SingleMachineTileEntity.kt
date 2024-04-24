package com.github.trcdevelopers.clayium.common.tileentity

import com.cleanroommc.modularui.api.drawable.IDrawable
import com.cleanroommc.modularui.api.drawable.IKey
import com.cleanroommc.modularui.factory.PosGuiData
import com.cleanroommc.modularui.screen.ModularPanel
import com.cleanroommc.modularui.utils.Alignment
import com.cleanroommc.modularui.value.sync.GuiSyncManager
import com.cleanroommc.modularui.value.sync.SyncHandlers
import com.cleanroommc.modularui.widget.Widget
import com.cleanroommc.modularui.widgets.ItemSlot
import com.cleanroommc.modularui.widgets.layout.Row
import com.github.trcdevelopers.clayium.common.ClayConstants
import com.github.trcdevelopers.clayium.common.gui.ClayGuiTextures
import com.github.trcdevelopers.clayium.common.recipe.registry.RecipeRegistry
import org.lwjgl.input.Keyboard

class Single2SingleMachineTileEntity : WorkableTileEntity() {
    override val inputSize: Int = 1
    override val outputSize: Int = 1

    override fun buildUI(data: PosGuiData, syncManager: GuiSyncManager): ModularPanel {
        workable.syncValues(syncManager)
        ceSlot.syncValues(syncManager)

        return ModularPanel("single_to_single_machine")
            .flex {
                it.align(Alignment.Center)
            }
            .child(IKey.lang("tile.clayium.${recipeRegistry.category.categoryName}", IKey.lang("${ClayConstants.MACHINE_TIER_LANG_KEY}$tier")).asWidget()
                .top(6)
                .left(6))
            .child(Row()
                .widthRel(0.6f).height(26)
                .align(Alignment.Center)
                .top(30)
                .child(Widget()
                    .size(26, 26)
                    .background(ClayGuiTextures.LARGE_SLOT)
                    .align(Alignment.CenterLeft))
                .child(ItemSlot().left(4).top(4)
                    .slot(SyncHandlers.itemSlot(inputInventory, 0)
                        .singletonSlotGroup(2))
                    .background(IDrawable.EMPTY))
                .child(workable.getProgressBar())
                .child(Widget()
                    .size(26, 26)
                    .background(ClayGuiTextures.LARGE_SLOT)
                    .align(Alignment.CenterRight))
                .child(ItemSlot().right(4).top(4)
                    .slot(SyncHandlers.itemSlot(outputInventory, 0)
                        .singletonSlotGroup(1))
                    .background(IDrawable.EMPTY)))
            .child(ceSlot.getSlotWidget()
                .right(7).top(58)
                .setEnabledIf {
                    // is shift key down
                    Keyboard.isKeyDown(42) || Keyboard.isKeyDown(54)
                }
                .background(IDrawable.EMPTY))
            .child(IKey.dynamic { IKey.lang(ClayConstants.CE_LANG_KEY, ceSlot.toString()).toString() }.asWidget()
                .widthRel(0.5f)
                .pos(6, 60))
            .bindPlayerInventory()
    }

    companion object {
        fun create(tier: Int, recipeRegistry: RecipeRegistry<*>): Single2SingleMachineTileEntity {
            return Single2SingleMachineTileEntity().apply {
                this.recipeRegistry = recipeRegistry
                this.initializeByTier(tier)
            }
        }
    }
}