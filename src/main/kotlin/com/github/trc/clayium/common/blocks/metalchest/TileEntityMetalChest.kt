package com.github.trc.clayium.common.blocks.metalchest

import com.cleanroommc.modularui.api.IGuiHolder
import com.cleanroommc.modularui.api.drawable.IKey
import com.cleanroommc.modularui.factory.PosGuiData
import com.cleanroommc.modularui.screen.ModularPanel
import com.cleanroommc.modularui.utils.Alignment
import com.cleanroommc.modularui.value.sync.GuiSyncManager
import com.cleanroommc.modularui.value.sync.SyncHandlers
import com.cleanroommc.modularui.widgets.ItemSlot
import com.cleanroommc.modularui.widgets.SlotGroupWidget
import com.cleanroommc.modularui.widgets.TextWidget
import com.cleanroommc.modularui.widgets.layout.Column
import com.github.trc.clayium.api.GUI_DEFAULT_WIDTH
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.EnumFacing
import net.minecraftforge.common.capabilities.Capability
import net.minecraftforge.items.CapabilityItemHandler.ITEM_HANDLER_CAPABILITY
import net.minecraftforge.items.ItemStackHandler

class TileEntityMetalChest() : TileEntity(), IGuiHolder<PosGuiData> {
    var customName: String? = null
    val itemInventory = ItemStackHandler(6*9)
    fun hasCustomName() : Boolean {
        return customName != null
    }
    fun getName() : String {
        return this.customName?: "container.chest"
    }
    override fun readFromNBT(compound: NBTTagCompound) {
        super.readFromNBT(compound);
        if (compound.hasKey("CustomName", 8))
        {
            this.customName = compound.getString("CustomName");
        }
    }

    override fun writeToNBT(compound: NBTTagCompound): NBTTagCompound {
        super.writeToNBT(compound)
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
    override fun buildUI(
        data: PosGuiData,
        syncManager: GuiSyncManager
    ): ModularPanel {
        syncManager.registerSlotGroup("metal_chest_inv", 6)
        val columnStr = "I".repeat(9)
        val matrixStr = (0..<6).map { columnStr }
        return ModularPanel.defaultPanel("metal_chest_inv", GUI_DEFAULT_WIDTH, 18 + 6 * 18 + 94 + 2)
            .child(
                TextWidget(IKey.lang("metal_chest"))
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
            .bindPlayerInventory()
    }



}