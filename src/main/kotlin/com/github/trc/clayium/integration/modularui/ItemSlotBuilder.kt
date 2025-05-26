package com.github.trc.clayium.integration.modularui

import com.cleanroommc.modularui.widgets.slot.IOnSlotChanged
import com.cleanroommc.modularui.widgets.slot.ItemSlot
import com.cleanroommc.modularui.widgets.slot.ModularSlot
import net.minecraft.item.ItemStack
import net.minecraftforge.items.IItemHandler
import java.util.function.Predicate

class ItemSlotBuilder(
    private val phantom: Boolean,
    private val itemHandler: IItemHandler,
    private val index: Int,
) {

    private val modularSlot = ModularSlot(itemHandler, index)

    fun build(): ItemSlot {
        return ItemSlot.create(phantom).slot(modularSlot)
    }

    fun slotGroup(slotGroup: String): ItemSlotBuilder {
        modularSlot.slotGroup(slotGroup)
        return this
    }

    fun filter(filter: Predicate<ItemStack>): ItemSlotBuilder {
        modularSlot.filter(filter)
        return this
    }

    fun changeListener(changeListener: IOnSlotChanged): ItemSlotBuilder {
        modularSlot.changeListener(changeListener)
        return this
    }

    fun putOnly(): ItemSlotBuilder {
        modularSlot.accessibility(true, false)
        return this
    }

    fun takeOnly(): ItemSlotBuilder {
        modularSlot.accessibility(false, true)
        return this
    }

    fun lock(): ItemSlotBuilder {
        modularSlot.accessibility(false, false)
        return this
    }
}