package io.github.trcdevelopers.clayium.common.items.filter

import com.cleanroommc.modularui.api.drawable.IKey
import com.cleanroommc.modularui.drawable.DynamicDrawable
import com.cleanroommc.modularui.drawable.GuiTextures
import com.cleanroommc.modularui.factory.HandGuiData
import com.cleanroommc.modularui.screen.ModularPanel
import com.cleanroommc.modularui.utils.Alignment
import com.cleanroommc.modularui.utils.ItemStackItemHandler
import com.cleanroommc.modularui.value.sync.PanelSyncManager
import com.cleanroommc.modularui.value.sync.SyncHandlers
import com.cleanroommc.modularui.widget.ParentWidget
import com.cleanroommc.modularui.widgets.CycleButtonWidget
import com.cleanroommc.modularui.widgets.SlotGroupWidget
import com.cleanroommc.modularui.widgets.layout.Column
import io.github.trcdevelopers.clayium.api.capability.IItemFilter
import io.github.trcdevelopers.clayium.api.capability.ItemCapabilityProvider
import io.github.trcdevelopers.clayium.common.capability.impl.ItemFilterSimple
import io.github.trcdevelopers.clayium.integration.modularui.MuiSlots
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraftforge.common.capabilities.Capability
import net.minecraftforge.common.capabilities.ICapabilityProvider
import net.minecraftforge.common.util.Constants
import net.minecraftforge.items.CapabilityItemHandler
import net.minecraftforge.items.IItemHandlerModifiable

class ItemSimpleItemFilter : ItemFilterBase(:: ItemFilterSimple) {
    override fun buildUI(data: HandGuiData, syncManager: PanelSyncManager): ModularPanel {
        val stack = data.usedItemStack
        val itemHandler = stack.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null) as? IItemHandlerModifiable
            ?: return ModularPanel.defaultPanel("simple_item_filter_error")

        syncManager.registerSlotGroup("filter", FILTER_SIZE_X)
        val matrix: Array<String> = "I".repeat(FILTER_SIZE_X).let { column ->
            (0..<FILTER_SIZE_Y).map { column }.toTypedArray()
        }

        val isWhiteListSyncHandler = SyncHandlers.bool(
            {
                val tag = data.usedItemStack.tagCompound
                if (tag == null || !tag.hasKey("isWhiteList", Constants.NBT.TAG_BYTE)) {
                    true
                } else {
                    tag.getBoolean("isWhiteList")
                }
            },
            { value ->
                val tag = stack.tagCompound ?: NBTTagCompound()
                tag.setBoolean("isWhiteList", value)
                stack.tagCompound = tag
            }
        )

        MuiSlots.lockHeldItem(syncManager, data.player)
        return ModularPanel.defaultPanel("simple_item_filter")
            .child(Column().margin(7)
                .child(ParentWidget().widthRel(1f).expanded().marginBottom(2)
                    .child(IKey.str(stack.displayName).asWidget()
                        .align(Alignment.TopLeft))
                    .child(IKey.lang("container.inventory").asWidget()
                        .align(Alignment.BottomLeft))
                    .child(CycleButtonWidget()
                        .length(2)
                        .align(Alignment.CenterRight)
                        .value(isWhiteListSyncHandler)
                        .overlay(DynamicDrawable {
                            if (isWhiteListSyncHandler.value) {
                                GuiTextures.FILTER
                            } else {
                                GuiTextures.CLOSE
                            }
                        })
                        .addTooltip(0, "Deny")
                        .addTooltip(1, "Allow")
                    )
                    .child(SlotGroupWidget.builder()
                        .matrix(*matrix)
                        .key('I') { i -> MuiSlots.phantomSlotBuilder(itemHandler, i).slotGroup("filter").build() }
                        .build()
                        .align(Alignment.Center)))
                .child(SlotGroupWidget.playerInventory(0, false)))
    }

    override fun createItemFilter(stack: ItemStack): IItemFilter {
        val itemHandler = stack.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null) as? IItemHandlerModifiable
        if (itemHandler == null) return ItemFilterSimple()

        val stacksMutableList = mutableListOf<ItemStack>()
        for (i in 0..<itemHandler.slots) {
            val handlerStack = itemHandler.getStackInSlot(i)
            if (!handlerStack.isEmpty) {
                stacksMutableList.add(handlerStack.copy())
            }
        }
        val tag = stack.tagCompound?.takeIf { it.hasKey("isWhiteList", Constants.NBT.TAG_BYTE) }
        val isWhiteList = tag?.getBoolean("isWhiteList") ?: true
        return ItemFilterSimple(stacksMutableList, isWhiteList)
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

    companion object {
        const val FILTER_SIZE_X = 5
        const val FILTER_SIZE_Y = 2
    }
}