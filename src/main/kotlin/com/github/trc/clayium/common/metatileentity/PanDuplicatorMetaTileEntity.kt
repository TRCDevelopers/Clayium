package com.github.trc.clayium.common.metatileentity

import com.cleanroommc.modularui.api.drawable.IKey
import com.cleanroommc.modularui.factory.PosGuiData
import com.cleanroommc.modularui.screen.ModularPanel
import com.cleanroommc.modularui.utils.Alignment
import com.cleanroommc.modularui.value.sync.GuiSyncManager
import com.cleanroommc.modularui.value.sync.SyncHandlers
import com.cleanroommc.modularui.widget.ParentWidget
import com.cleanroommc.modularui.widgets.ItemSlot
import com.cleanroommc.modularui.widgets.ProgressWidget
import com.cleanroommc.modularui.widgets.SlotGroupWidget
import com.cleanroommc.modularui.widgets.layout.Column
import com.cleanroommc.modularui.widgets.layout.Row
import com.github.trc.clayium.api.CValues
import com.github.trc.clayium.api.capability.impl.ClayEnergyHolder
import com.github.trc.clayium.api.capability.impl.ItemHandlerProxy
import com.github.trc.clayium.api.capability.impl.NotifiableItemStackHandler
import com.github.trc.clayium.api.metatileentity.AutoIoHandler
import com.github.trc.clayium.api.metatileentity.MetaTileEntity
import com.github.trc.clayium.api.pan.IPan
import com.github.trc.clayium.api.pan.IPanNode
import com.github.trc.clayium.api.util.ITier
import com.github.trc.clayium.common.gui.ClayGuiTextures
import net.minecraft.item.Item
import net.minecraft.util.ResourceLocation
import net.minecraftforge.items.wrapper.CombinedInvWrapper

class PanDuplicatorMetaTileEntity(
    metaTileEntityId: ResourceLocation,
    tier: ITier,
) : MetaTileEntity(metaTileEntityId, tier, onlyNoneList, onlyNoneList, "${CValues.MOD_ID}.pan_duplicator"), IPanNode {

    private val antimatterSlot = NotifiableItemStackHandler(this, 1, this, isExport = false)
    private val duplicationTargetSlot = NotifiableItemStackHandler(this, 1, this, isExport = true)

    override val importItems = CombinedInvWrapper(antimatterSlot, duplicationTargetSlot)
    override val exportItems = NotifiableItemStackHandler(this, 1, this, isExport = true)
    override val itemInventory = ItemHandlerProxy(importItems, exportItems)

    @Suppress("unused")
    private val ioHandler = AutoIoHandler.Combined(this)
    private val clayEnergyHolder = ClayEnergyHolder(this)

    private var pan: IPan? = null

    override fun createMetaTileEntity(): MetaTileEntity {
        return PanDuplicatorMetaTileEntity(metaTileEntityId, tier)
    }

    override fun registerItemModel(item: Item, meta: Int) {
    }

    override fun buildUI(data: PosGuiData, syncManager: GuiSyncManager): ModularPanel {
        return ModularPanel.defaultPanel("pan_duplicator")
            .child(Column().margin(7).sizeRel(1f)
                .child(ParentWidget().widthRel(1f).expanded().marginBottom(2)
                    .child(IKey.lang(this.translationKey, IKey.lang(tier.prefixTranslationKey)).asWidget()
                        .align(Alignment.TopLeft))
                    .child(IKey.lang("container.inventory").asWidget()
                        .align(Alignment.BottomLeft))
                    .child(clayEnergyHolder.createCeTextWidget(syncManager)
                        .bottom(12).left(0).widthRel(0.5f))
                    .child(Row().widthRel(0.7f).height(26).align(Alignment.Center)
                        .child(SlotGroupWidget.builder()
                            .row("AD")
                            .key('A', ItemSlot().slot(SyncHandlers.itemSlot(antimatterSlot, 0).singletonSlotGroup())
                                .background(ClayGuiTextures.IMPORT_1_SLOT))
                            .key('D', ItemSlot().slot(SyncHandlers.itemSlot(duplicationTargetSlot, 0).singletonSlotGroup())
                                .background(ClayGuiTextures.IMPORT_2_SLOT))
                            .build()
                            .align(Alignment.CenterLeft)
                        )
                        .child(largeSlot(SyncHandlers.itemSlot(exportItems, 0).singletonSlotGroup().accessibility(false, true))
                            .align(Alignment.CenterRight))
                        .child(ProgressWidget()
                            .progress(0.0)
                            .size(22, 17).align(Alignment.Center)
                            .texture(ClayGuiTextures.PROGRESS_BAR, 22)
                        )
                    )
                )
                .child(SlotGroupWidget.playerInventory(0))
            )
    }

    override fun setNetwork(network: IPan) {
        pan = network
    }

    override fun resetNetwork() {
        pan = null
    }
}