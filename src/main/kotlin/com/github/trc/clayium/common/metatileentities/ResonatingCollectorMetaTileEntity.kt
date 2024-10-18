package com.github.trc.clayium.common.metatileentities

import com.cleanroommc.modularui.api.drawable.IKey
import com.cleanroommc.modularui.utils.Alignment
import com.cleanroommc.modularui.utils.NumberFormat
import com.cleanroommc.modularui.value.sync.GuiSyncManager
import com.cleanroommc.modularui.value.sync.SyncHandlers
import com.cleanroommc.modularui.widget.ParentWidget
import com.cleanroommc.modularui.widgets.ItemSlot
import com.cleanroommc.modularui.widgets.SlotGroupWidget
import com.github.trc.clayium.api.MOD_ID
import com.github.trc.clayium.api.capability.impl.ResonanceManager
import com.github.trc.clayium.api.metatileentity.AbstractItemGeneratorMetaTileEntity
import com.github.trc.clayium.api.metatileentity.MetaTileEntity
import com.github.trc.clayium.api.unification.OreDictUnifier
import com.github.trc.clayium.api.unification.material.CMaterials
import com.github.trc.clayium.api.unification.ore.OrePrefix
import com.github.trc.clayium.api.util.ITier
import com.github.trc.clayium.api.util.asWidgetResizing
import com.github.trc.clayium.api.util.clayiumId
import net.minecraft.client.resources.I18n
import net.minecraft.util.EnumFacing
import net.minecraft.util.ResourceLocation

class ResonatingCollectorMetaTileEntity(
    metaTileEntityId: ResourceLocation,
    tier: ITier,
) : AbstractItemGeneratorMetaTileEntity(metaTileEntityId, tier, name = "resonating_collector") {
    val resonanceManager = ResonanceManager(this, 2)

    override val faceTexture = clayiumId("blocks/ca_resonating_collector")

    // these are used in superclass to create itemHandler, so they have a custom getter
    override val inventoryColumnSize
        get() = 3

    override val inventoryRowSize
        get() = 3

    override val progressPerItem = 10_000
    override val progressPerTick: Int
        get() = (resonanceManager.resonance - 1.0).coerceAtMost(Int.MAX_VALUE.toDouble()).toInt()

    override val generatingItem by lazy { OreDictUnifier.get(OrePrefix.gem, CMaterials.antimatter) }

    override fun isTerrainValid() = true

    override fun isFacingValid(facing: EnumFacing): Boolean {
        return facing.axis.isHorizontal
    }

    override fun createMetaTileEntity(): MetaTileEntity {
        return ResonatingCollectorMetaTileEntity(metaTileEntityId, tier)
    }

    override fun buildMainParentWidget(syncManager: GuiSyncManager): ParentWidget<*> {
        resonanceManager.sync(syncManager)
        return super.buildMainParentWidget(syncManager)
            .child(
                IKey.dynamic {
                        I18n.format(
                            "gui.$MOD_ID.resonance",
                            NumberFormat.formatWithMaxDigits(resonanceManager.resonance)
                        )
                    }
                    .asWidgetResizing()
                    .align(Alignment.BottomRight)
            )
            .child(
                SlotGroupWidget.builder()
                    .matrix("III", "III", "III")
                    .key('I') { i ->
                        ItemSlot()
                            .slot(
                                SyncHandlers.itemSlot(itemInventory, i)
                                    .slotGroup("machine_inventory")
                            )
                    }
                    .build()
                    .align(Alignment.Center)
            )
    }
}
