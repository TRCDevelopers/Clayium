package io.github.trcdevelopers.clayium.common.metatileentities

import com.cleanroommc.modularui.api.drawable.IKey
import com.cleanroommc.modularui.utils.Alignment
import com.cleanroommc.modularui.value.sync.PanelSyncManager
import com.cleanroommc.modularui.widget.ParentWidget
import com.cleanroommc.modularui.widgets.SlotGroupWidget
import io.github.trcdevelopers.clayium.api.MOD_ID
import io.github.trcdevelopers.clayium.api.capability.impl.ResonanceManager
import io.github.trcdevelopers.clayium.api.metatileentity.AbstractItemGeneratorMetaTileEntity
import io.github.trcdevelopers.clayium.api.metatileentity.MetaTileEntity
import io.github.trcdevelopers.clayium.api.metatileentity.MteRenderingConfig
import io.github.trcdevelopers.clayium.api.unification.OreDictUnifier
import io.github.trcdevelopers.clayium.api.unification.material.CMaterials
import io.github.trcdevelopers.clayium.api.unification.ore.OrePrefix
import io.github.trcdevelopers.clayium.api.util.ITier
import io.github.trcdevelopers.clayium.api.util.clayiumId
import io.github.trcdevelopers.clayium.common.util.CNumberFormat
import io.github.trcdevelopers.clayium.common.util.SidelessI18n
import io.github.trcdevelopers.clayium.integration.modularui.MuiSlots
import net.minecraft.util.EnumFacing
import net.minecraft.util.ResourceLocation
import java.math.RoundingMode

private val numberFormatter = CNumberFormat.DEFAULT.copyToBuilder()
    .roundingMode(RoundingMode.DOWN)
    .decimalFormatPatternProvider { unit: CNumberFormat.DisplayUnit, displayValue: Double ->
        when (unit) {
            CNumberFormat.DisplayUnit.NoUnit -> {
                when {
                    displayValue < 10.0 -> "0.000" // 1.234
                    displayValue < 100.0 -> "0.00" // 12.34
                    else -> "0.0" // 123.4
                }
            }
            CNumberFormat.DisplayUnit.ScientificNotation -> "0.00"
            is CNumberFormat.DisplayUnit.Symbol -> {
                when {
                    displayValue < 10.0 -> "0.00" // 1.23k
                    displayValue < 100.0 -> "0.0" // 12.3k
                    else -> "0" // 123k
                }
            }
        }
    }
    .build()

class ResonatingCollectorMetaTileEntity(
    metaTileEntityId: ResourceLocation,
    tier: ITier,
) : AbstractItemGeneratorMetaTileEntity(metaTileEntityId, tier, name = "resonating_collector") {

    val resonanceManager = ResonanceManager(this, 2)

    // these are used in superclass to create itemHandler, so they have a custom getter
    override val inventoryColumnSize get() = 3
    override val inventoryRowSize get() = 3

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

    override fun buildMainParentWidget(syncManager: PanelSyncManager): ParentWidget<*> {
        resonanceManager.sync(syncManager)
        return super.buildMainParentWidget(syncManager)
            .child(IKey.dynamic {
                SidelessI18n.format("gui.$MOD_ID.resonance", numberFormatter.format(resonanceManager.resonance))
            }.asWidget().width(90).alignment(Alignment.BottomRight)
                .align(Alignment.BottomRight))
            .child(SlotGroupWidget.builder()
                .matrix("III", "III", "III")
                .key('I') { i ->
                    MuiSlots.itemSlotBuilder(itemInventory, i)
                        .slotGroup("machine_inventory").build()
                }.build().align(Alignment.Center))
    }

    override val renderingConfig by lazy {
        MteRenderingConfig.face(clayiumId("blocks/ca_resonating_collector"))
    }
}