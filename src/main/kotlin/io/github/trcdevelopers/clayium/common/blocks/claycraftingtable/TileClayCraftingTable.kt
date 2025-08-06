package io.github.trcdevelopers.clayium.common.blocks.claycraftingtable

import com.cleanroommc.modularui.api.drawable.IDrawable
import com.cleanroommc.modularui.api.drawable.IKey
import com.cleanroommc.modularui.api.widget.IGuiAction
import com.cleanroommc.modularui.factory.PosGuiData
import com.cleanroommc.modularui.screen.ModularPanel
import com.cleanroommc.modularui.utils.Alignment
import com.cleanroommc.modularui.value.sync.PanelSyncManager
import com.cleanroommc.modularui.widget.ParentWidget
import com.cleanroommc.modularui.widgets.ProgressWidget
import com.cleanroommc.modularui.widgets.SlotGroupWidget
import com.cleanroommc.modularui.widgets.layout.Column
import com.cleanroommc.modularui.widgets.layout.Row
import com.cleanroommc.modularui.widgets.slot.ItemSlot
import com.cleanroommc.modularui.widgets.slot.ModularSlot
import io.github.trcdevelopers.clayium.api.MOD_ID
import io.github.trcdevelopers.clayium.api.capability.impl.ClayiumItemStackHandler
import io.github.trcdevelopers.clayium.api.metatileentity.interfaces.IMarkDirty
import io.github.trcdevelopers.clayium.api.util.Mods
import io.github.trcdevelopers.clayium.common.gui.ClayGuiTextures
import io.github.trcdevelopers.clayium.common.util.DummyContainer
import io.github.trcdevelopers.clayium.integration.jei.JeiPlugin
import io.github.trcdevelopers.clayium.integration.modularui.IGuiHolderClayium
import io.github.trcdevelopers.clayium.integration.modularui.MuiSlots
import mezz.jei.api.recipe.VanillaRecipeCategoryUid
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.inventory.InventoryCrafting
import net.minecraft.item.ItemStack
import net.minecraft.item.crafting.CraftingManager
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.tileentity.TileEntity

class TileClayCraftingTable : TileEntity(), IMarkDirty, IGuiHolderClayium<PosGuiData> {
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

    override fun buildUI(data: PosGuiData, syncManager: PanelSyncManager): ModularPanel {
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
                                MuiSlots.itemSlotBuilder(inputInventory, i)
                                    .slotGroup("input_inventory")
                                    .changeListener { newItem, onlyAmountChanged, client, init ->
                                        onInputSlotChanged()
                                    }.build()
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
                            // Overriding onTake, so not using ItemSlotBuilder
                            .child(ItemSlot.create(false).align(Alignment.Center)
                                .slot(object: ModularSlot(outputInventory, 0) {
                                        override fun onTake(thePlayer: EntityPlayer, stack: ItemStack): ItemStack {
                                            onOutputSlotTake()
                                            return super.onTake(thePlayer, stack)
                                        }
                                    }.accessibility(false, true))
                                .background(IDrawable.EMPTY))
                            .align(Alignment.CenterRight))
                    )
                )
                .child(MuiSlots.playerInventory(0))
            )
    }

    private fun onInputSlotChanged() {
        val matrix = InventoryCrafting(DummyContainer, 3, 3)
        for (slot in 0..<9) matrix.setInventorySlotContents(slot, inputInventory.getStackInSlot(slot))
        val recipe = CraftingManager.findMatchingRecipe(matrix, world)
        if (recipe == null) {
            outputInventory.setStackInSlot(0, ItemStack.EMPTY)
        } else {
            val output = recipe.getCraftingResult(matrix)
            outputInventory.setStackInSlot(0, output)
        }
        markAsDirty()
    }

    private fun onOutputSlotTake() {
        val matrix = InventoryCrafting(DummyContainer, 3, 3)
        for (slot in 0..<9) matrix.setInventorySlotContents(slot, inputInventory.getStackInSlot(slot))
        val recipe = CraftingManager.findMatchingRecipe(matrix, world) ?: return
        recipe.getRemainingItems(matrix).forEachIndexed { i, stack ->
            inputInventory.setStackInSlot(i, stack)
        }
    }

    override fun markAsDirty() {
        this.markDirty()
    }
}