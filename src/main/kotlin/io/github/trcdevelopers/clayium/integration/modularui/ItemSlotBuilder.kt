package io.github.trcdevelopers.clayium.integration.modularui

import com.cleanroommc.modularui.api.drawable.IDrawable
import com.cleanroommc.modularui.utils.Alignment
import com.cleanroommc.modularui.widget.ParentWidget
import com.cleanroommc.modularui.widgets.slot.IOnSlotChanged
import com.cleanroommc.modularui.widgets.slot.ItemSlot
import com.cleanroommc.modularui.widgets.slot.ModularCraftingSlot
import com.cleanroommc.modularui.widgets.slot.ModularSlot
import com.cleanroommc.modularui.widgets.slot.SlotGroup
import io.github.trcdevelopers.clayium.common.gui.ClayGuiTextures
import net.minecraft.item.ItemStack
import net.minecraftforge.items.IItemHandler
import org.jetbrains.annotations.ApiStatus
import java.util.function.Predicate

class ItemSlotBuilder(
    private val phantom: Boolean,
    private val itemHandler: IItemHandler,
    private val index: Int,
) {

    private var modularSlot = ModularSlot(itemHandler, index)

    fun build(): ItemSlot {
        return ItemSlot.create(phantom).slot(modularSlot)
    }

    fun buildLarge(): ParentWidget<*> {
        return ParentWidget()
            .size(26, 26)
            .background(ClayGuiTextures.LARGE_SLOT)
            .child(ItemSlot.create(phantom).align(Alignment.Center)
                .slot(modularSlot)
                .background(IDrawable.EMPTY))
    }

    @Suppress("UnstableApiUsage")
    @ApiStatus.Experimental
    fun ignoreMaxStackSize(): ItemSlotBuilder {
        modularSlot.ignoreMaxStackSize(true)
        return this
    }

    fun craftingSlot(): ItemSlotBuilder {
        this.modularSlot = ModularCraftingSlot(itemHandler, index)
        return this
    }

    fun slotGroup(slotGroup: String): ItemSlotBuilder {
        modularSlot.slotGroup(slotGroup)
        return this
    }

    fun slotGroup(slotGroup: SlotGroup): ItemSlotBuilder {
        modularSlot.slotGroup(slotGroup)
        return this
    }

    fun singletonSlotGroup(shiftClickPriority: Int = SlotGroup.STORAGE_SLOT_PRIO): ItemSlotBuilder {
        modularSlot.singletonSlotGroup(shiftClickPriority)
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