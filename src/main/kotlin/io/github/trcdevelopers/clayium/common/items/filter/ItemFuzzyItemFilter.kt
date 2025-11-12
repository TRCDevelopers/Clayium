package io.github.trcdevelopers.clayium.common.items.filter

import com.cleanroommc.modularui.api.drawable.IKey
import com.cleanroommc.modularui.factory.PlayerInventoryGuiData
import com.cleanroommc.modularui.screen.ModularPanel
import com.cleanroommc.modularui.utils.Alignment
import com.cleanroommc.modularui.utils.ItemStackItemHandler
import com.cleanroommc.modularui.value.sync.PanelSyncManager
import com.cleanroommc.modularui.widget.ParentWidget
import com.cleanroommc.modularui.widgets.SlotGroupWidget
import com.cleanroommc.modularui.widgets.layout.Column
import io.github.trcdevelopers.clayium.api.capability.IItemFilter
import io.github.trcdevelopers.clayium.api.capability.ItemCapabilityProvider
import io.github.trcdevelopers.clayium.common.capability.impl.ItemFilterFuzzy
import io.github.trcdevelopers.clayium.common.items.filter.ItemSimpleItemFilter.Companion.FILTER_SIZE_X
import io.github.trcdevelopers.clayium.common.items.filter.ItemSimpleItemFilter.Companion.FILTER_SIZE_Y
import io.github.trcdevelopers.clayium.integration.modularui.MuiSlots
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraftforge.common.capabilities.Capability
import net.minecraftforge.common.capabilities.ICapabilityProvider
import net.minecraftforge.items.CapabilityItemHandler
import net.minecraftforge.items.IItemHandlerModifiable

class ItemFuzzyItemFilter : ItemFilterBase(::ItemFilterFuzzy) {
    override fun buildUI(data: PlayerInventoryGuiData, syncManager: PanelSyncManager): ModularPanel {
        val stack = data.usedItemStack
        val itemHandler = stack.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null) as? IItemHandlerModifiable
            ?: return ModularPanel.defaultPanel("item_filter_fuzzy_error")

        syncManager.registerSlotGroup("filter", FILTER_SIZE_X)
        val matrix: Array<String> = "I".repeat(FILTER_SIZE_X).let { column ->
            (0..<FILTER_SIZE_Y).map { column }.toTypedArray()
        }

        MuiSlots.lockHeldItem(syncManager, data.player)
        return ModularPanel.defaultPanel("item_filter_fuzzy")
            .child(Column().margin(7)
                .child(ParentWidget().widthRel(1f).expanded().marginBottom(2)
                    .child(IKey.str(stack.displayName).asWidget()
                        .align(Alignment.TopLeft))
                    .child(IKey.lang("container.inventory").asWidget()
                        .align(Alignment.BottomLeft))
                    .child(SlotGroupWidget.builder()
                        .matrix(*matrix)
                        .key('I') { i -> MuiSlots.phantomSlotBuilder(itemHandler, i).slotGroup("filter").build() }
                        .build()
                        .align(Alignment.Center)))
                .child(MuiSlots.playerInventory(0)))
    }

    override fun createItemFilter(stack: ItemStack): IItemFilter {
        val itemHandler = stack.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null) as? IItemHandlerModifiable
        if (itemHandler == null) return IItemFilter.ALWAYS_FALSE

        val stacksMutableList = mutableListOf<ItemStack>()
        for (i in 0..<itemHandler.slots) {
            val handlerStack = itemHandler.getStackInSlot(i)
            if (!handlerStack.isEmpty) {
                stacksMutableList.add(handlerStack.copy())
            }
        }
        return ItemFilterFuzzy(stacksMutableList)
    }

    override fun initCapabilities(stack: ItemStack, nbt: NBTTagCompound?): ICapabilityProvider? {
        val superProvider = super.initCapabilities(stack, nbt)
        return object : ItemCapabilityProvider {
            override fun <T> getCapability(capability: Capability<T>): T? {
                return if (capability === CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
                    return capability.cast(ItemStackItemHandler(stack, FILTER_SIZE_X * FILTER_SIZE_Y))
                } else {
                    superProvider?.getCapability(capability, null)
                }
            }
        }
    }
}