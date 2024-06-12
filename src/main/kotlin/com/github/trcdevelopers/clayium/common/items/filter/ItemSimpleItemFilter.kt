package com.github.trcdevelopers.clayium.common.items.filter

import com.cleanroommc.modularui.api.IGuiHolder
import com.cleanroommc.modularui.api.drawable.IKey
import com.cleanroommc.modularui.factory.HandGuiData
import com.cleanroommc.modularui.factory.ItemGuiFactory
import com.cleanroommc.modularui.screen.ModularPanel
import com.cleanroommc.modularui.utils.Alignment
import com.cleanroommc.modularui.utils.ItemCapabilityProvider
import com.cleanroommc.modularui.utils.ItemStackItemHandler
import com.cleanroommc.modularui.value.sync.GuiSyncManager
import com.cleanroommc.modularui.value.sync.SyncHandlers
import com.cleanroommc.modularui.widget.ParentWidget
import com.cleanroommc.modularui.widgets.ItemSlot
import com.cleanroommc.modularui.widgets.SlotGroupWidget
import com.cleanroommc.modularui.widgets.layout.Column
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.entity.player.EntityPlayerMP
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.ActionResult
import net.minecraft.util.EnumActionResult
import net.minecraft.util.EnumHand
import net.minecraft.world.World
import net.minecraftforge.common.capabilities.Capability
import net.minecraftforge.common.capabilities.ICapabilityProvider
import net.minecraftforge.items.CapabilityItemHandler
import net.minecraftforge.items.IItemHandlerModifiable

class ItemSimpleItemFilter : Item(), IGuiHolder<HandGuiData> {
    override fun buildUI(data: HandGuiData, syncManager: GuiSyncManager): ModularPanel {
        val itemHandler = data.usedItemStack.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null) as? IItemHandlerModifiable
            ?: return ModularPanel.defaultPanel("simple_item_filter_error")

        syncManager.registerSlotGroup("filter", FILTER_SIZE_X)
        val matrix: Array<String> = "I".repeat(FILTER_SIZE_X).let { column ->
            (0..<FILTER_SIZE_Y).map { column }.toTypedArray()
        }

        return ModularPanel.defaultPanel("simple_item_filter")
            .child(Column().margin(7)
                .child(ParentWidget().widthRel(1f).expanded().marginBottom(2)
                    .child(IKey.str(data.usedItemStack.displayName).asWidget()
                        .align(Alignment.TopLeft))
                    .child(IKey.lang("container.inventory").asWidget()
                        .align(Alignment.BottomLeft))
                    .child(SlotGroupWidget.builder()
                        .matrix(*matrix)
                        .key('I') { i -> ItemSlot().slot(SyncHandlers.phantomItemSlot(itemHandler, i)
                            .slotGroup("filter"))
                        }
                        .build()
                        .align(Alignment.Center)))
                .child(SlotGroupWidget.playerInventory(0)))
    }

    override fun onItemRightClick(worldIn: World, playerIn: EntityPlayer, handIn: EnumHand): ActionResult<ItemStack> {
        if (!worldIn.isRemote) {
            ItemGuiFactory.open(playerIn as EntityPlayerMP, handIn)
        }
        return ActionResult(EnumActionResult.SUCCESS, playerIn.getHeldItem(handIn))
    }

    override fun initCapabilities(stack: ItemStack, nbt: NBTTagCompound?): ICapabilityProvider? {
        return object : ItemCapabilityProvider {
            override fun <T> getCapability(capability: Capability<T>): T? {
                if (capability === CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
                    return CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.cast(
                        object : ItemStackItemHandler(stack, FILTER_SIZE_X * FILTER_SIZE_Y) {
                            override fun getSlotLimit(slot: Int) = 1
                        })
                }
                return null
            }
        }
    }

    private companion object {
        private const val FILTER_SIZE_X = 5
        private const val FILTER_SIZE_Y = 2
    }
}