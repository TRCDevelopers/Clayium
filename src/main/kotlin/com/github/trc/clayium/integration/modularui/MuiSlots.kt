package com.github.trc.clayium.integration.modularui

import com.cleanroommc.modularui.widgets.SlotGroupWidget
import com.cleanroommc.modularui.widgets.slot.ItemSlot
import com.cleanroommc.modularui.widgets.slot.ModularSlot
import net.minecraftforge.items.IItemHandler

object MuiSlots {
    fun itemSlot(handler: IItemHandler, index: Int): ItemSlot {
        return ItemSlot.create(false).slot(ModularSlot(handler, index))
    }

    fun phantomSlot(handler: IItemHandler, index: Int): ItemSlot {
        return ItemSlot.create(true).slot(ModularSlot(handler, index))
    }

    fun itemSlotBuilder(handler: IItemHandler, index: Int): ItemSlotBuilder {
        return ItemSlotBuilder(phantom = false, itemHandler = handler, index = index)
    }

    fun phantomSlotBuilder(handler: IItemHandler, index: Int): ItemSlotBuilder {
        return ItemSlotBuilder(phantom = true, itemHandler = handler, index = index)
    }

    fun playerInventory(bottom: Int): SlotGroupWidget {
        return SlotGroupWidget.playerInventory(bottom, false)
    }
}