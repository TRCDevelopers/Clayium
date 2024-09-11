package com.github.trc.clayium.common.metatileentities

import com.cleanroommc.modularui.utils.Alignment
import com.cleanroommc.modularui.value.sync.GuiSyncManager
import com.cleanroommc.modularui.value.sync.SyncHandlers
import com.cleanroommc.modularui.widget.ParentWidget
import com.cleanroommc.modularui.widgets.ItemSlot
import com.github.trc.clayium.api.capability.impl.EmptyItemStackHandler
import com.github.trc.clayium.api.capability.impl.VoidingItemHandler
import com.github.trc.clayium.api.metatileentity.MetaTileEntity
import com.github.trc.clayium.api.metatileentity.trait.AutoIoHandler
import com.github.trc.clayium.api.util.ITier
import net.minecraft.item.ItemStack
import net.minecraft.util.ResourceLocation
import net.minecraftforge.items.IItemHandlerModifiable
import net.minecraftforge.items.ItemHandlerHelper
import net.minecraftforge.items.ItemStackHandler

class VoidContainerMetaTileEntity(
    metaTileEntityId: ResourceLocation,
    tier: ITier,
) : MetaTileEntity(metaTileEntityId, tier, bufferValidInputModes, onlyNoneList, "void_container")  {
    override val importItems: IItemHandlerModifiable = VoidContainerItemHandler()
    override val exportItems = EmptyItemStackHandler
    override val itemInventory = importItems

    private val autoIoHandler: AutoIoHandler = AutoIoHandler.Combined(this)
    private val filterSlot = ItemStackHandler(1)
    private val filterStack get() = filterSlot.getStackInSlot(0)

    override fun createMetaTileEntity(): MetaTileEntity {
        return VoidContainerMetaTileEntity(metaTileEntityId, tier)
    }

    override fun buildMainParentWidget(syncManager: GuiSyncManager): ParentWidget<*> {
        return super.buildMainParentWidget(syncManager)
            .child(largeSlot(SyncHandlers.itemSlot(importItems, 0)
                .filter { filterStack.isEmpty || ItemHandlerHelper.canItemStacksStack(it, filterStack) })
                .align(Alignment.Center))
            .child(ItemSlot().slot(SyncHandlers.phantomItemSlot(filterSlot, 0))
                .right(10).top(15))
    }

    private inner class VoidContainerItemHandler : VoidingItemHandler() {
        override fun insertItem(slot: Int, stack: ItemStack, simulate: Boolean): ItemStack {
            if (filterStack.isEmpty || ItemHandlerHelper.canItemStacksStack(stack, filterStack)) {
                return ItemStack.EMPTY
            }
            return stack
        }
    }
}