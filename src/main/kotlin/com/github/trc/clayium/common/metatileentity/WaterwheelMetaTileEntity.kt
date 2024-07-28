package com.github.trc.clayium.common.metatileentity

import com.cleanroommc.modularui.api.drawable.IKey
import com.cleanroommc.modularui.factory.PosGuiData
import com.cleanroommc.modularui.screen.ModularPanel
import com.cleanroommc.modularui.utils.Alignment
import com.cleanroommc.modularui.value.sync.GuiSyncManager
import com.cleanroommc.modularui.widget.ParentWidget
import com.cleanroommc.modularui.widgets.SlotGroupWidget
import com.cleanroommc.modularui.widgets.layout.Column
import com.github.trc.clayium.api.CValues
import com.github.trc.clayium.api.capability.impl.EmptyItemStackHandler
import com.github.trc.clayium.api.metatileentity.MetaTileEntity
import com.github.trc.clayium.api.util.ITier
import com.github.trc.clayium.api.util.clayiumId
import net.minecraft.item.Item
import net.minecraft.util.ResourceLocation

class WaterwheelMetaTileEntity(
    metaTileEntityId: ResourceLocation,
    tier: ITier,
) : MetaTileEntity(
    metaTileEntityId, tier, onlyNoneList, onlyNoneList,
    "machine.${CValues.MOD_ID}.waterwheel",
) {
    override val faceTexture = clayiumId("blocks/waterwheel")
    override val importItems = EmptyItemStackHandler
    override val exportItems = EmptyItemStackHandler
    override val itemInventory = EmptyItemStackHandler

    private var waterCount = 0
    private var progress = 0
    private val durability = 1000

    override fun createMetaTileEntity(): MetaTileEntity {
        return WaterwheelMetaTileEntity(metaTileEntityId, tier)
    }

    override fun registerItemModel(item: Item, meta: Int) {
        registerItemModelDefault(item, meta, "waterwheel")
    }

    override fun buildUI(data: PosGuiData, syncManager: GuiSyncManager): ModularPanel {
        return ModularPanel.defaultPanel("waterwheel")
            .child(Column().margin(7)
                .child(ParentWidget().widthRel(1f).expanded().marginBottom(2)
                    .child(IKey.lang(this.translationKey, IKey.lang(tier.prefixTranslationKey)).asWidget()
                        .align(Alignment.TopLeft))
                    .child(IKey.lang("container.inventory").asWidget()
                        .align(Alignment.BottomLeft))
                    .child(IKey.lang("gui.clayium.waterwheel.waters", waterCount).asWidget()
                        .align(Alignment.BottomRight))
                    .child(IKey.lang("gui.clayium.waterwheel.progress", progress).asWidget()
                        .align(Alignment.CenterLeft))
                    .child(IKey.lang("gui.clayium.waterwheel.durability", durability).asWidget()
                        .align(Alignment.CenterRight))
                )
                .child(SlotGroupWidget.playerInventory(0))
            )
    }

    companion object {
        private const val MAX_PROGRESS = 20_000
    }
}