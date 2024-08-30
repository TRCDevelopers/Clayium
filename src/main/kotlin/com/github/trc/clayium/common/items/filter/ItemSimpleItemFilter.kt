package com.github.trc.clayium.common.items.filter

import com.cleanroommc.modularui.api.IGuiHolder
import com.cleanroommc.modularui.api.drawable.IKey
import com.cleanroommc.modularui.drawable.DynamicDrawable
import com.cleanroommc.modularui.drawable.GuiTextures
import com.cleanroommc.modularui.factory.HandGuiData
import com.cleanroommc.modularui.factory.ItemGuiFactory
import com.cleanroommc.modularui.screen.ModularPanel
import com.cleanroommc.modularui.utils.Alignment
import com.cleanroommc.modularui.utils.ItemCapabilityProvider
import com.cleanroommc.modularui.utils.ItemStackItemHandler
import com.cleanroommc.modularui.value.sync.GuiSyncManager
import com.cleanroommc.modularui.value.sync.SyncHandlers
import com.cleanroommc.modularui.widget.ParentWidget
import com.cleanroommc.modularui.widgets.CycleButtonWidget
import com.cleanroommc.modularui.widgets.ItemSlot
import com.cleanroommc.modularui.widgets.SlotGroupWidget
import com.cleanroommc.modularui.widgets.layout.Column
import com.github.trc.clayium.api.capability.ClayiumCapabilities
import com.github.trc.clayium.api.capability.IItemFilter
import com.github.trc.clayium.api.capability.impl.SimpleItemFilter
import com.github.trc.clayium.api.util.getMetaTileEntity
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.entity.player.EntityPlayerMP
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.ActionResult
import net.minecraft.util.EnumActionResult
import net.minecraft.util.EnumFacing
import net.minecraft.util.EnumHand
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import net.minecraftforge.common.capabilities.Capability
import net.minecraftforge.common.capabilities.ICapabilityProvider
import net.minecraftforge.common.util.Constants
import net.minecraftforge.items.CapabilityItemHandler
import net.minecraftforge.items.IItemHandlerModifiable

class ItemSimpleItemFilter : Item(), IGuiHolder<HandGuiData> {
    override fun buildUI(data: HandGuiData, syncManager: GuiSyncManager): ModularPanel {
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

    override fun onItemUseFirst(player: EntityPlayer, world: World, pos: BlockPos, side: EnumFacing, hitX: Float, hitY: Float, hitZ: Float, hand: EnumHand): EnumActionResult {
        val metaTileEntity = world.getMetaTileEntity(pos) ?: return EnumActionResult.PASS
        if (world.isRemote) return EnumActionResult.SUCCESS
        metaTileEntity.setFilter(side, createFilter(player.getHeldItem(hand)), FilterType.SIMPLE)
        return EnumActionResult.SUCCESS
    }

    private fun createFilter(stack: ItemStack): IItemFilter {
        val itemHandler = stack.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null) as? IItemHandlerModifiable
        if (itemHandler == null) return SimpleItemFilter()

        val stacksMutableList = mutableListOf<ItemStack>()
        for (i in 0..<itemHandler.slots) {
            val handlerStack = itemHandler.getStackInSlot(i)
            if (!handlerStack.isEmpty) {
                stacksMutableList.add(handlerStack.copy())
            }
        }
        val tag = stack.tagCompound?.takeIf { it.hasKey("isWhiteList", Constants.NBT.TAG_BYTE) }
        val isWhiteList = if (tag == null) true else tag.getBoolean("isWhiteList")
        return SimpleItemFilter(stacksMutableList, isWhiteList)
    }

    override fun initCapabilities(stack: ItemStack, nbt: NBTTagCompound?): ICapabilityProvider? {
        return object : ItemCapabilityProvider {
            override fun <T> getCapability(capability: Capability<T>): T? {
                return when {
                    capability === CapabilityItemHandler.ITEM_HANDLER_CAPABILITY
                        -> capability.cast(ItemStackItemHandler(stack, FILTER_SIZE_X * FILTER_SIZE_Y))
                    capability === ClayiumCapabilities.ITEM_FILTER
                        -> capability.cast(createFilter(stack))
                    else -> null
                }
            }
        }
    }

    private companion object {
        private const val FILTER_SIZE_X = 5
        private const val FILTER_SIZE_Y = 2
    }
}