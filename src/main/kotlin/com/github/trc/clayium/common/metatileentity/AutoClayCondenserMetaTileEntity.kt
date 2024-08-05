package com.github.trc.clayium.common.metatileentity

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
import com.github.trc.clayium.api.capability.impl.NotifiableItemStackHandler
import com.github.trc.clayium.api.metatileentity.MetaTileEntity
import com.github.trc.clayium.api.util.CUtils
import com.github.trc.clayium.api.util.ITier
import com.github.trc.clayium.common.blocks.ItemBlockMaterial
import com.github.trc.clayium.common.unification.OreDictUnifier
import com.github.trc.clayium.common.unification.material.Material
import com.github.trc.clayium.common.unification.material.PropertyKey
import com.github.trc.clayium.common.unification.ore.OrePrefix
import it.unimi.dsi.fastutil.ints.IntArrayList
import it.unimi.dsi.fastutil.objects.Object2IntAVLTreeMap
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.util.ResourceLocation
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import net.minecraftforge.items.ItemHandlerHelper

class AutoClayCondenserMetaTileEntity(
    metaTileEntityId: ResourceLocation,
    tier: ITier,
) : MetaTileEntity(metaTileEntityId, tier, validInputModesLists[1], validOutputModesLists[1],
    "machine.${CValues.MOD_ID}.auto_clay_condenser") {
    override val itemInventory = NotifiableItemStackHandler(this, 16, this, isExport = false)
    override val importItems = itemInventory
    override val exportItems = itemInventory

    override fun update() {
        super.update()
        onServer {
            if (!hasNotifiedInputs) return
            // gather slot nums that contains clay
            val slotNums = IntArrayList(itemInventory.slots)
            for (i in 0..<itemInventory.slots) {
                val stack = itemInventory.getStackInSlot(i)
                val m = getMaterial(stack)
                if (m?.getPropOrNull(PropertyKey.CLAY) != null) { slotNums.add(i) }
            }
            // compress clays
            val clay2Amount = Object2IntAVLTreeMap<Material>(CUtils.MATERIAL_TIER_ASC)
            for (i in slotNums) {
                val stack = itemInventory.getStackInSlot(i)
                val material = getMaterial(stack) ?: continue
                if (material.getPropOrNull(PropertyKey.CLAY) == null) continue
                clay2Amount.addTo(material, stack.count)
                itemInventory.extractItem(i, stack.count, false)
            }
            for ((material, amount) in clay2Amount) {
                val clay = material.getPropOrNull(PropertyKey.CLAY) ?: continue
                if (clay.compressedInto == null) continue
                val nextTierAmount = amount / 9
                val remainingAmount = amount % 9
                if (nextTierAmount > 0) {
                    val nextTierMaterial = clay.compressedInto
                    clay2Amount.addTo(nextTierMaterial, nextTierAmount)
                    clay2Amount[material] = remainingAmount
                }
            }
            for ((material, amount) in clay2Amount) {
                ItemHandlerHelper.insertItem(itemInventory, OreDictUnifier.get(OrePrefix.block, material, amount), false)
            }
        }
    }

    @SideOnly(Side.CLIENT)
    override fun registerItemModel(item: Item, meta: Int) {
    }

    override fun buildUI(data: PosGuiData, syncManager: GuiSyncManager): ModularPanel {
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
                            ItemSlot().slot(SyncHandlers.itemSlot(itemInventory, it))
                        }
                        .build().align(Alignment.Center))
                )
                .child(SlotGroupWidget.playerInventory(0))
            )
    }

    override fun createMetaTileEntity(): MetaTileEntity {
        return AutoClayCondenserMetaTileEntity(metaTileEntityId, tier)
    }

    private fun getMaterial(stack: ItemStack): Material? {
        if (stack.isEmpty) return null
        val item = stack.item
        if (item !is ItemBlockMaterial) return null
        return item.blockMaterial.getCMaterial(stack)
    }
}