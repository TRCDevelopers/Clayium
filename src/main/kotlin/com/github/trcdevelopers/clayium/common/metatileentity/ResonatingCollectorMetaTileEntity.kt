package com.github.trcdevelopers.clayium.common.metatileentity

import com.cleanroommc.modularui.api.drawable.IKey
import com.cleanroommc.modularui.factory.PosGuiData
import com.cleanroommc.modularui.screen.ModularPanel
import com.cleanroommc.modularui.utils.Alignment
import com.cleanroommc.modularui.utils.NumberFormat
import com.cleanroommc.modularui.value.sync.GuiSyncManager
import com.cleanroommc.modularui.value.sync.SyncHandlers
import com.cleanroommc.modularui.widget.ParentWidget
import com.cleanroommc.modularui.widgets.ItemSlot
import com.cleanroommc.modularui.widgets.SlotGroupWidget
import com.cleanroommc.modularui.widgets.layout.Column
import com.github.trcdevelopers.clayium.api.CValues
import com.github.trcdevelopers.clayium.api.capability.impl.ResonanceManager
import com.github.trcdevelopers.clayium.api.metatileentity.AbstractItemGeneratorMetaTileEntity
import com.github.trcdevelopers.clayium.api.metatileentity.MetaTileEntity
import com.github.trcdevelopers.clayium.api.util.CUtils.clayiumId
import com.github.trcdevelopers.clayium.api.util.ITier
import com.github.trcdevelopers.clayium.common.unification.OreDictUnifier
import com.github.trcdevelopers.clayium.common.unification.material.CMaterials
import com.github.trcdevelopers.clayium.common.unification.ore.OrePrefix
import net.minecraft.client.renderer.block.model.ModelResourceLocation
import net.minecraft.client.resources.I18n
import net.minecraft.item.Item
import net.minecraft.util.EnumFacing
import net.minecraft.util.ResourceLocation
import net.minecraftforge.client.model.ModelLoader
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly

class ResonatingCollectorMetaTileEntity(
    metaTileEntityId: ResourceLocation,
    tier: ITier,
) : AbstractItemGeneratorMetaTileEntity(metaTileEntityId, tier, translationKey = "machine.${CValues.MOD_ID}.resonating_collector") {
    val resonanceManager = ResonanceManager(this, 2)

    override val faceTexture = clayiumId("blocks/ca_resonating_collector")

    // these are used in super class to create itemHandler, so they have custom getter
    override val inventoryColumnSize get() = 3
    override val inventoryRowSize get() = 3

    override val progressPerItem = 10_000
    override val progressPerTick: Int
        get() = (resonanceManager.resonance - 1.0).toInt()
    override val generatingItem by lazy { OreDictUnifier.get(OrePrefix.matter, CMaterials.antimatter) }

    override fun isTerrainValid() = true

    override fun isFacingValid(facing: EnumFacing): Boolean {
        return facing.axis.isHorizontal
    }

    override fun createMetaTileEntity(): MetaTileEntity {
        return ResonatingCollectorMetaTileEntity(metaTileEntityId, tier)
    }

    @SideOnly(Side.CLIENT)
    override fun registerItemModel(item: Item, meta: Int) {
        ModelLoader.setCustomModelResourceLocation(item, meta, ModelResourceLocation(clayiumId("resonating_collector"), "tier=${tier.lowerName}"))
    }

    override fun buildUI(data: PosGuiData, syncManager: GuiSyncManager): ModularPanel {
        syncManager.registerSlotGroup("machine_inventory", 3)
        return ModularPanel.defaultPanel("resonating_collector")
            .child(Column().margin(7)
                .child(ParentWidget().widthRel(1f).expanded().marginBottom(2)
                    .child(IKey.lang(this.translationKey).asWidget()
                        .align(Alignment.TopLeft))
                    .child(IKey.lang("container.inventory").asWidget()
                        .align(Alignment.BottomLeft))
                    .child(IKey.dynamic {
                        I18n.format("gui.${CValues.MOD_ID}.resonance", NumberFormat.formatWithMaxDigits(resonanceManager.resonance))
                    }.asWidget()
                        .align(Alignment.BottomRight))
                    .child(SlotGroupWidget.builder()
                        .matrix("III", "III", "III")
                        .key('I') { i ->
                            ItemSlot().slot(SyncHandlers.itemSlot(itemInventory, i)
                            .slotGroup("machine_inventory"))
                        }.build().align(Alignment.Center))
                )
                .child(SlotGroupWidget.playerInventory(0))
            )
    }
}