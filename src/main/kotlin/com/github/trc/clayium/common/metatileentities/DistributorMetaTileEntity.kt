package com.github.trc.clayium.common.metatileentities

import com.cleanroommc.modularui.screen.ModularPanel
import com.cleanroommc.modularui.utils.Alignment
import com.cleanroommc.modularui.value.sync.GuiSyncManager
import com.cleanroommc.modularui.value.sync.SyncHandlers
import com.cleanroommc.modularui.widget.ParentWidget
import com.cleanroommc.modularui.widgets.ItemSlot
import com.cleanroommc.modularui.widgets.SlotGroupWidget
import com.cleanroommc.modularui.widgets.layout.Column
import com.cleanroommc.modularui.widgets.layout.Row
import com.github.trc.clayium.api.GUI_DEFAULT_HEIGHT
import com.github.trc.clayium.api.GUI_DEFAULT_WIDTH
import com.github.trc.clayium.api.capability.impl.ClayiumItemStackHandler
import com.github.trc.clayium.api.gui.data.MetaTileEntityGuiData
import com.github.trc.clayium.api.metatileentity.MetaTileEntity
import com.github.trc.clayium.api.util.ITier
import net.minecraft.util.EnumFacing
import net.minecraft.util.ResourceLocation
import net.minecraftforge.common.capabilities.Capability
import net.minecraftforge.items.CapabilityItemHandler
import net.minecraftforge.items.wrapper.CombinedInvWrapper
import kotlin.math.min

class DistributorMetaTileEntity(
    metaTileEntityId: ResourceLocation,
    tier: ITier,
) : MetaTileEntity(metaTileEntityId, tier, bufferValidInputModes, validOutputModesLists[1], "distributor") {

    private val groupX = when (tier.numeric) {
        7, 8, 9 -> tier.numeric - 5
        else -> 1
    }
    private val groupY = when (tier.numeric) {
        7, 8 -> 2
        9 -> 3
        else -> 1
    }

    private val groups = List(groupX * groupY) { ClayiumItemStackHandler(this, 4) }

    override val itemInventory = CombinedInvWrapper(*groups.toTypedArray())
    override val importItems = itemInventory
    override val exportItems = itemInventory

    override fun buildUI(data: MetaTileEntityGuiData, syncManager: GuiSyncManager): ModularPanel {
        val height = GUI_DEFAULT_HEIGHT - 50 + (18*2 * groupY + 2 * (groupY - 1))
        return ModularPanel.defaultPanel(translationKey, GUI_DEFAULT_WIDTH, height)
            .columnWithPlayerInv {
                child(buildMainParentWidget(syncManager))
            }
    }

    override fun buildMainParentWidget(syncManager: GuiSyncManager): ParentWidget<*> {
        val groups = groups.mapIndexed { i, handler ->
            syncManager.registerSlotGroup("group$i", 2)
            val group = SlotGroupWidget.builder()
                .matrix("II", "II")
                .key('I') { j ->
                    ItemSlot().slot(SyncHandlers.itemSlot(handler, j).slotGroup("group$i"))
                }
                .build()
                .debugName("group$i")
            group
        }
        val slotGroupRows = groups.windowed(this.groupX, this.groupX).map { slotGroupList ->
            val row = Row()
                .size((18 * 2) * this.groupX + 2 * (this.groupX - 1), 18 * 2)
            slotGroupList.forEachIndexed { i, g ->
                row.child(g.marginLeft(2 * min(i, 1)))
            }
            row
        }
        val column = Column()
            .width(18 * 2 * this.groupX + 2 * (this.groupX - 1))
            .height(18 * 2 * this.groupY + 2 * (this.groupY - 1))
        val w = super.buildMainParentWidget(syncManager)
        slotGroupRows.forEachIndexed { i, row ->
            column.child(row.marginTop(2 * min(i, 1)))
        }
        return w.child(column.align(Alignment.Center))
    }

    override fun <T> getCapability(capability: Capability<T>, facing: EnumFacing?): T? {
        if (capability === CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            // don't expose inventory
            return null
        }
        return super.getCapability(capability, facing)
    }

    override fun createMetaTileEntity(): MetaTileEntity {
        return DistributorMetaTileEntity(this.metaTileEntityId, this.tier)
    }
}