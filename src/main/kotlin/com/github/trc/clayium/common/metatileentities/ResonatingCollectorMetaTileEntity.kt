package com.github.trc.clayium.common.metatileentities

import com.cleanroommc.modularui.api.drawable.IKey
import com.cleanroommc.modularui.factory.PosGuiData
import com.cleanroommc.modularui.screen.ModularPanel
import com.cleanroommc.modularui.utils.Alignment
import com.cleanroommc.modularui.utils.NumberFormat
import com.cleanroommc.modularui.value.sync.GuiSyncManager
import com.cleanroommc.modularui.value.sync.SyncHandlers
import com.cleanroommc.modularui.widgets.ItemSlot
import com.cleanroommc.modularui.widgets.SlotGroupWidget
import com.github.trc.clayium.api.CValues
import com.github.trc.clayium.api.capability.impl.ResonanceManager
import com.github.trc.clayium.api.metatileentity.AbstractItemGeneratorMetaTileEntity
import com.github.trc.clayium.api.metatileentity.MetaTileEntity
import com.github.trc.clayium.api.unification.OreDictUnifier
import com.github.trc.clayium.api.unification.material.CMaterials
import com.github.trc.clayium.api.unification.ore.OrePrefix
import com.github.trc.clayium.api.util.ITier
import com.github.trc.clayium.api.util.clayiumId
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
    override val generatingItem by lazy { OreDictUnifier.get(OrePrefix.gem, CMaterials.antimatter) }

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
            .child(mainColumn {
                child(buildMainParentWidget(syncManager)
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
            })
    }
}