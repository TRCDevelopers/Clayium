package com.github.trcdevelopers.clayium.common.blocks

import com.cleanroommc.modularui.api.IGuiHolder
import com.cleanroommc.modularui.factory.PosGuiData
import com.cleanroommc.modularui.screen.ModularPanel
import com.cleanroommc.modularui.utils.Alignment
import com.cleanroommc.modularui.value.sync.GuiSyncManager
import com.cleanroommc.modularui.value.sync.SyncHandlers
import com.cleanroommc.modularui.widgets.ItemSlot
import com.github.trcdevelopers.clayium.api.capability.impl.InfiniteItemStackHandler
import net.minecraft.item.ItemStack
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.EnumFacing
import net.minecraftforge.common.capabilities.Capability
import net.minecraftforge.items.CapabilityItemHandler

class TileEntityCreativeEnergySource : TileEntity(), IGuiHolder<PosGuiData> {
    private val handler = InfiniteItemStackHandler(ItemStack(ClayiumBlocks.ENERGIZED_CLAY, 64, 8))
    override fun buildUI(data: PosGuiData, syncManager: GuiSyncManager): ModularPanel {
        return ModularPanel.defaultPanel("clayium:creative_energy_source")
            .child(ItemSlot()
                .slot(SyncHandlers.itemSlot(handler, 0).accessibility(false, true))
                .align(Alignment.TopCenter).top(36))
            .bindPlayerInventory()
    }

    override fun <T> getCapability(capability: Capability<T?>, facing: EnumFacing?): T? {
        return if (capability === CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.cast(handler)
        } else {
            super.getCapability(capability, facing)
        }
    }
}