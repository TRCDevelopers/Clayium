package com.github.trc.clayium.common.blocks.metalchest

import com.cleanroommc.modularui.api.IGuiHolder
import com.cleanroommc.modularui.api.drawable.IKey
import com.cleanroommc.modularui.factory.PosGuiData
import com.cleanroommc.modularui.screen.ModularPanel
import com.cleanroommc.modularui.utils.Alignment
import com.cleanroommc.modularui.value.sync.GuiSyncManager
import com.cleanroommc.modularui.value.sync.SyncHandlers
import com.cleanroommc.modularui.widgets.ButtonWidget
import com.cleanroommc.modularui.widgets.ItemSlot
import com.cleanroommc.modularui.widgets.SlotGroupWidget
import com.cleanroommc.modularui.widgets.TextWidget
import com.cleanroommc.modularui.widgets.layout.Column
import com.github.trc.clayium.api.util.toList
import net.minecraft.inventory.ItemStackHelper
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.EnumFacing
import net.minecraft.util.NonNullList
import net.minecraftforge.common.capabilities.Capability
import net.minecraftforge.items.CapabilityItemHandler.ITEM_HANDLER_CAPABILITY
import net.minecraftforge.items.ItemStackHandler
import kotlin.math.max

class TileEntityMetalChest(
    val inventoryRowSize: Int,
    val inventoryColumnSize: Int,
    val inventoryPage: Int

) : TileEntity(), IGuiHolder<PosGuiData> {
    var customName: String? = null

    val itemInventory = ItemStackHandler(inventoryRowSize*inventoryColumnSize*inventoryPage)
    fun hasCustomName() : Boolean {
        return customName != null
    }
    fun getName() : String {
        return this.customName?: "container.chest"
    }
    override fun readFromNBT(compound: NBTTagCompound) {
        super.readFromNBT(compound);
        val list = NonNullList.withSize(inventoryRowSize*inventoryColumnSize*inventoryPage, ItemStack.EMPTY)
        ItemStackHelper.loadAllItems(compound, list)
        list.forEachIndexed { slot, stack ->
            this.itemInventory.insertItem(slot, stack, false)
        }
        if (compound.hasKey("CustomName", 8))
        {
            this.customName = compound.getString("CustomName");
        }
    }

    override fun writeToNBT(compound: NBTTagCompound): NBTTagCompound {
        super.writeToNBT(compound)
        var list = itemInventory.toList()
        ItemStackHelper.saveAllItems(compound, NonNullList.from<ItemStack>(ItemStack.EMPTY, *list.toTypedArray()))
        if (this.hasCustomName())
        {
            compound.setString("CustomName", this.customName!!);
        }

        return compound;
    }

    override fun <T> getCapability(capability: Capability<T>, facing: EnumFacing?): T? {
        if (capability === ITEM_HANDLER_CAPABILITY) {
            return capability.cast(itemInventory)
        }
        return super.getCapability(capability, facing)
    }

    override fun hasCapability(capability: Capability<*>, facing: EnumFacing?): Boolean {
        if (capability === ITEM_HANDLER_CAPABILITY) {
            return true
        }
        return super.hasCapability(capability, facing)
    }
    override fun buildUI(data: PosGuiData, syncManager: GuiSyncManager
    ): ModularPanel {
        syncManager.registerSlotGroup("metal_chest_inv", inventoryRowSize)
        val columnStr = "I".repeat(inventoryColumnSize)
        val matrixStr = (0..<inventoryRowSize).map { columnStr }
        return ModularPanel.defaultPanel("metal_chest_inv", 18 * max(inventoryColumnSize,9) + 14, 18 + inventoryRowSize * 18 + 94 + 2)
            .child(
                TextWidget(if (hasCustomName()) IKey.str(customName!!) else IKey.lang("metal_chest"))
                    .margin(6)
                    .align(Alignment.TopLeft))
            .child(Column()
                .marginTop(18)
                .child(SlotGroupWidget.builder()
                    .matrix(*matrixStr.toTypedArray())
                    .key('I') { index ->
                        ItemSlot().slot(
                            SyncHandlers.itemSlot(itemInventory, index)
                                .slotGroup("metal_chest_inv")
                        )
                    }
                    .build())
                .child(
                    TextWidget(IKey.lang("container.inventory"))
                        .paddingTop(1)
                        .paddingBottom(1)
                        .left(6)))
            .child(ButtonWidget()
                .marginTop(18 * inventoryRowSize + 30 + 2/* For centering */)
                .marginLeft(18 * 9 + 9 * max(-1,inventoryColumnSize-10) + 18)
                .size(14,14)
            )
            .child(TextWidget(IKey.str("<"))
                .marginTop(18 * inventoryRowSize + 30 + 2 + 4)
                .marginLeft(18 * 9 + 9 * max(-1,inventoryColumnSize-10) + 18 + 4)
            )
            .child(ButtonWidget()
                .marginTop(18 * inventoryRowSize + 30 + 2/* For centering */)
                .marginLeft(18 * 9 + 9 * max(-1,inventoryColumnSize-10) + 18 + 15)
                .size(14,14)
            )
            .child(TextWidget(IKey.str(">"))
                .marginTop(18 * inventoryRowSize + 30 + 2 + 4)
                .marginLeft(18 * 9 + 9 * max(-1,inventoryColumnSize-10) + 18 + 15 + 6)
            )
            .child(TextWidget(IKey.str("1 / $inventoryPage"))
                .marginTop(18 * inventoryRowSize + 30 + 2 + 15)
                .marginLeft(18 * 9 + 9 * max(-1,inventoryColumnSize-10) + 18)
            )
            .bindPlayerInventory()
    }



}