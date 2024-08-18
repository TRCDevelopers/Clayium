package com.github.trc.clayium.common.metatileentities

import com.cleanroommc.modularui.api.drawable.IKey
import com.cleanroommc.modularui.factory.PosGuiData
import com.cleanroommc.modularui.screen.ModularPanel
import com.cleanroommc.modularui.utils.Alignment
import com.cleanroommc.modularui.value.sync.GuiSyncManager
import com.cleanroommc.modularui.value.sync.SyncHandlers
import com.cleanroommc.modularui.widget.ParentWidget
import com.cleanroommc.modularui.widgets.ItemSlot
import com.cleanroommc.modularui.widgets.SlotGroupWidget
import com.cleanroommc.modularui.widgets.layout.Column
import com.github.trc.clayium.api.CValues
import com.github.trc.clayium.api.capability.impl.ClayiumItemStackHandler
import com.github.trc.clayium.api.capability.impl.NotifiableItemStackHandler
import com.github.trc.clayium.api.metatileentity.MetaTileEntity
import com.github.trc.clayium.api.unification.OreDictUnifier
import com.github.trc.clayium.api.unification.material.CMaterial
import com.github.trc.clayium.api.unification.material.CPropertyKey
import com.github.trc.clayium.api.unification.ore.OrePrefix
import com.github.trc.clayium.api.util.ITier
import com.github.trc.clayium.api.util.canStackWith
import com.github.trc.clayium.api.util.clayiumId
import com.github.trc.clayium.common.blocks.ItemBlockMaterial
import com.github.trc.clayium.common.gui.ClayGuiTextures
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.util.ResourceLocation
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import net.minecraftforge.items.ItemHandlerHelper
import kotlin.math.min

class AutoClayCondenserMetaTileEntity(
    metaTileEntityId: ResourceLocation,
    tier: ITier,
) : MetaTileEntity(metaTileEntityId, tier, validInputModesLists[1], validOutputModesLists[1],
    "machine.${CValues.MOD_ID}.auto_clay_condenser") {

    override val faceTexture = clayiumId("blocks/auto_clay_condenser")

    override val itemInventory = object : NotifiableItemStackHandler(this, 16, this, isExport = false) {
        override fun isItemValid(slot: Int, stack: ItemStack): Boolean {
            return getMaterial(stack)?.getPropOrNull(CPropertyKey.CLAY) != null
        }
    }
    override val importItems = itemInventory
    override val exportItems = itemInventory

    private val maxCompressedClay = object : ClayiumItemStackHandler(this, 1) {
        override fun isItemValid(slot: Int, stack: ItemStack): Boolean {
            return getMaterial(stack)?.getPropOrNull(CPropertyKey.CLAY) != null
        }
    }

    override fun update() {
        super.update()
        if (isRemote) return
        if (!hasNotifiedInputs) return
        sortInv()
        // compress clays
        val maxCompress = getMaterial(maxCompressedClay.getStackInSlot(0))?.tier?.numeric ?: Int.MAX_VALUE
        for (i in 0..<itemInventory.slots) {
            val stack = itemInventory.getStackInSlot(i)
            if (stack.isEmpty) break // inventory is sorted

            val m = getMaterial(stack) ?: continue
            if ((m.tier?.numeric ?: Int.MAX_VALUE) >= maxCompress) continue

            val clay = m.getPropOrNull(CPropertyKey.CLAY) ?: continue
            val compressedClay = clay.compressedInto ?: continue
            // slow down the process for lower tier
            val compressedAmount = if (this.tier.numeric >= 7) { stack.count / 9 } else { min(stack.count / 9, 1) }
            if (compressedAmount > 0) {
                val remain = ItemHandlerHelper.insertItem(itemInventory,
                    OreDictUnifier.get(OrePrefix.block, compressedClay, compressedAmount), true)
                if (!remain.isEmpty) break
                ItemHandlerHelper.insertItem(itemInventory,
                    OreDictUnifier.get(OrePrefix.block, compressedClay, compressedAmount), false)
                itemInventory.extractItem(i, compressedAmount * 9, false)
            }
        }
    }

    override fun buildUI(data: PosGuiData, syncManager: GuiSyncManager): ModularPanel {
        syncManager.registerSlotGroup("compressor_inventory", 4)
        return ModularPanel.defaultPanel("auto_clay_condenser", 176, 190)
            .child(Column().margin(7).sizeRel(1f)
                .child(ParentWidget().widthRel(1f).expanded().marginBottom(2)
                    .child(IKey.lang(this.translationKey, IKey.lang(tier.prefixTranslationKey)).asWidget()
                        .align(Alignment.TopLeft))
                    .child(IKey.lang("container.inventory").asWidget()
                        .align(Alignment.BottomLeft))
                    .child(SlotGroupWidget.builder()
                        .matrix("IIII", "IIII", "IIII", "IIII")
                        .key('I') {
                            ItemSlot().slot(SyncHandlers.itemSlot(itemInventory, it)
                                .filter { getMaterial(it)?.getPropOrNull(CPropertyKey.CLAY) != null }
                                .slotGroup("compressor_inventory"))
                        }
                        .build().align(Alignment.Center))
                    .child(ItemSlot().slot(SyncHandlers.phantomItemSlot(maxCompressedClay, 0)
                        .filter { getMaterial(it)?.getPropOrNull(CPropertyKey.CLAY) != null })
                        .align(Alignment.TopRight)
                        .background(ClayGuiTextures.CLAY_SLOT))
                )
                .child(SlotGroupWidget.playerInventory(0))
            )
    }

    override fun createMetaTileEntity(): MetaTileEntity {
        return AutoClayCondenserMetaTileEntity(metaTileEntityId, tier)
    }

    private fun getMaterial(stack: ItemStack): CMaterial? {
        if (stack.isEmpty) return null
        val item = stack.item
        if (item !is ItemBlockMaterial) return null
        return item.blockMaterial.getCMaterial(stack)
    }

    private fun sortInv() {
        for (i in 0..<itemInventory.slots) {
            val sortingStack = itemInventory.getStackInSlot(i)
            if (sortingStack.isEmpty) continue
            val remainSorting = sortingStack.copy()
            for (j in 0..<i) {
                val targetStack = itemInventory.getStackInSlot(j)
                if (!targetStack.canStackWith(sortingStack)) continue
                val amountExtracted = min(targetStack.maxStackSize - targetStack.count, remainSorting.count)
                itemInventory.extractItem(i, amountExtracted, false)
                itemInventory.insertItem(j, remainSorting.splitStack(amountExtracted), false)
                if (remainSorting.isEmpty) break
            }
        }
    }

    @SideOnly(Side.CLIENT)
    override fun registerItemModel(item: Item, meta: Int) {
        registerItemModelDefault(item, meta, "auto_clay_condenser")
    }
}