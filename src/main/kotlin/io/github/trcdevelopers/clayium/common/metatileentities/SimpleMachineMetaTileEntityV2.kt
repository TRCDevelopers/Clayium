package io.github.trcdevelopers.clayium.common.metatileentities

import com.cleanroommc.modularui.utils.Alignment
import com.cleanroommc.modularui.value.sync.PanelSyncManager
import com.cleanroommc.modularui.widget.ParentWidget
import com.cleanroommc.modularui.widgets.SlotGroupWidget
import com.cleanroommc.modularui.widgets.layout.Row
import io.github.trcdevelopers.clayium.api.ClayEnergy
import io.github.trcdevelopers.clayium.api.capability.impl.ItemHandlerProxy
import io.github.trcdevelopers.clayium.api.capability.impl.NotifiableItemStackHandler
import io.github.trcdevelopers.clayium.api.metatileentity.MetaTileEntity
import io.github.trcdevelopers.clayium.api.recipe.IClayiumRecipe
import io.github.trcdevelopers.clayium.api.recipe.IRecipeRegistry
import io.github.trcdevelopers.clayium.api.recipe.OverclockableRecipeProcessor
import io.github.trcdevelopers.clayium.api.recipe.RecipeProvider
import io.github.trcdevelopers.clayium.api.recipe.RecipeV2
import io.github.trcdevelopers.clayium.api.recipe.WorkableV2
import io.github.trcdevelopers.clayium.api.util.ITier
import io.github.trcdevelopers.clayium.api.util.MachineIoMode
import io.github.trcdevelopers.clayium.common.gui.ClayGuiTextures
import io.github.trcdevelopers.clayium.common.recipe.SimpleRecipeOutputs
import io.github.trcdevelopers.clayium.common.recipe.ingredient.COreRecipeInput
import io.github.trcdevelopers.clayium.integration.modularui.MuiSlots
import net.minecraft.init.Items
import net.minecraft.item.ItemStack
import net.minecraft.util.ResourceLocation

class SimpleMachineMetaTileEntityV2(
    metaTileEntityId: ResourceLocation,
    tier: ITier,
) : MetaTileEntity(
    metaTileEntityId, tier, validInputModesLists[1], validOutputModesLists[1], "simple_machine_v2"
) {
    override val importItems = NotifiableItemStackHandler(this, 1, this, false)
    override val exportItems = NotifiableItemStackHandler(this, 1, this, true)
    override val itemInventory = ItemHandlerProxy(importItems, exportItems)

    val workable: WorkableV2 = WorkableV2(
        this, OverclockableRecipeProcessor(this.overclockHandler),
        RecipeProvider(
            this, this.importItems, this.exportItems, TestRecipeRegistry()
        )
    )

    override fun createMetaTileEntity(): MetaTileEntity {
        return SimpleMachineMetaTileEntityV2(this.metaTileEntityId, this.tier)
    }

    override fun buildMainParentWidget(syncManager: PanelSyncManager): ParentWidget<*> {
        val slotsAndProgressBar = Row()
            .widthRel(0.7f).height(26)
            .align(Alignment.Center)
            .child(workable.progressBar(syncManager).align(Alignment.Center))

        if (importItems.slots == 1) {
            slotsAndProgressBar.child(
                MuiSlots.itemSlotBuilder(importItems, 0).singletonSlotGroup().build().align(Alignment.CenterLeft)
            )
        } else if (importItems.slots == 2) {
            syncManager.registerSlotGroup("input_inv", 1)
            slotsAndProgressBar.child(
                SlotGroupWidget.builder()
                    .matrix("II").key('I') { index ->
                        MuiSlots.itemSlotBuilder(importItems, index).slotGroup("input_inv").build()
                            .apply {
                                if (index == 0) background(ClayGuiTextures.IMPORT_1_SLOT) else background(ClayGuiTextures.IMPORT_2_SLOT)
                            }}
                    .build()
                    .align(Alignment.CenterLeft))
        }
        if (exportItems.slots == 1) {
            slotsAndProgressBar.child(
                MuiSlots.itemSlotBuilder(exportItems, 0).singletonSlotGroup().takeOnly().buildLarge()
                    .align(Alignment.CenterRight))
        } else if (exportItems.slots == 2) {
            syncManager.registerSlotGroup("output_inv", 1)
            slotsAndProgressBar.child(
                SlotGroupWidget.builder()
                    .matrix("II").key('I') { index ->
                        MuiSlots.itemSlotBuilder(exportItems, index)
                            .slotGroup("output_inv")
                            .takeOnly()
                            .build()
                            .apply {
                                if (index == 0) background(ClayGuiTextures.EXPORT_1_SLOT) else background(ClayGuiTextures.EXPORT_2_SLOT)
                            }
                    }
                    .build()
                    .align(Alignment.CenterRight)
            )
        }

        return super.buildMainParentWidget(syncManager)
            .child(slotsAndProgressBar.align(Alignment.Center))
//            .child(clayEnergyHolder.createCeTextWidget(syncManager)
//                .debugName("CE Text")
//                .bottom(12).left(0))
//            .child(clayEnergyHolder.createSlotWidget()
//                .debugName("CE Slot")
//                .align(Alignment.BottomRight))
//            .childIf(tier.numeric < 3, ButtonWidget()
//                .size(16, 16).align(Alignment.BottomCenter)
//                .overlay(ClayGuiTextures.CE_BUTTON)
//                .hoverOverlay(ClayGuiTextures.CE_BUTTON_HOVERED)
//                .syncHandler(InteractionSyncHandler().setOnMousePressed {
//                    clayEnergyHolder.addEnergy(ClayEnergy(1))
//                }))
    }
}

class TestRecipeRegistry : IRecipeRegistry {
    override val jeiCategory: String = ""
    val recipe = RecipeV2(
        listOf(COreRecipeInput("ingotIron", 1)),
        SimpleRecipeOutputs(ItemStack(Items.GOLD_INGOT)),
        20,
        ClayEnergy.of(1),
        0,
        0,
    )

    override fun searchRecipe(machineTier: Int, inputs: List<ItemStack>): IClayiumRecipe? {
        if (machineTier < recipe.recipeTier) return null
        val copied = inputs.map { it.copy() }

        for (ingredient in recipe.inputs) {
            if (!ingredient.isConsumable) continue
            var matched = false
            for (stack in copied) {
                if (ingredient.testItemStackAndAmount(stack)) {
                    stack.shrink(ingredient.consumeAmount)
                    matched = true
                    break
                }
            }
            if (!matched) return null
        }
        return recipe
    }

}
