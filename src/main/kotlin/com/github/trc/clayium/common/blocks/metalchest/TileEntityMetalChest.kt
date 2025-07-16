package com.github.trc.clayium.common.blocks.metalchest

import com.cleanroommc.modularui.api.IGuiHolder
import com.cleanroommc.modularui.api.drawable.IKey
import com.cleanroommc.modularui.factory.PosGuiData
import com.cleanroommc.modularui.screen.ModularPanel
import com.cleanroommc.modularui.screen.UISettings
import com.cleanroommc.modularui.utils.Alignment
import com.cleanroommc.modularui.value.sync.PanelSyncManager
import com.cleanroommc.modularui.value.sync.SyncHandlers
import com.cleanroommc.modularui.widget.ParentWidget
import com.cleanroommc.modularui.widgets.ButtonWidget
import com.cleanroommc.modularui.widgets.PagedWidget
import com.cleanroommc.modularui.widgets.SlotGroupWidget
import com.cleanroommc.modularui.widgets.layout.Column
import com.cleanroommc.modularui.widgets.slot.ItemSlot
import com.github.trc.clayium.api.GUI_DEFAULT_WIDTH
import com.github.trc.clayium.api.unification.material.CMaterial
import com.github.trc.clayium.api.util.asWidgetResizing
import com.github.trc.clayium.api.util.toList
import com.github.trc.clayium.integration.modularui.MuiSlots
import net.minecraft.entity.EntityLivingBase
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
    val inventoryPage: Int,
    val material: CMaterial,
) : TileEntity(), IGuiHolder<PosGuiData> {

    private var customName: String? = null
    private val itemInventory = ItemStackHandler(inventoryRowSize * inventoryColumnSize * inventoryPage)

    var prevLidAngle = 0f
        private set
    var lidAngle = 0f
        private set
    var facing: EnumFacing = EnumFacing.NORTH
        private set

    fun hasCustomName(): Boolean {
        return customName != null
    }

    fun getName(): String {
        return this.customName ?: "container.chest"
    }

    fun onBlockPlacedBy(placer: EntityLivingBase) {
        this.facing = placer.horizontalFacing.opposite
    }

    override fun readFromNBT(compound: NBTTagCompound) {
        super.readFromNBT(compound)
        val list = NonNullList.withSize(inventoryRowSize * inventoryColumnSize * inventoryPage, ItemStack.EMPTY)
        ItemStackHelper.loadAllItems(compound, list)
        list.forEachIndexed { slot, stack ->
            this.itemInventory.insertItem(slot, stack, false)
        }
        if (compound.hasKey("CustomName", 8)) {
            this.customName = compound.getString("CustomName")
        }
    }

    override fun writeToNBT(compound: NBTTagCompound): NBTTagCompound {
        super.writeToNBT(compound)
        val list = itemInventory.toList()
        ItemStackHelper.saveAllItems(compound, NonNullList.from<ItemStack>(ItemStack.EMPTY, *list.toTypedArray()))
        if (this.hasCustomName()) {
            compound.setString("CustomName", this.customName!!)
        }

        return compound
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

    override fun buildUI(data: PosGuiData, syncManager: PanelSyncManager, settings: UISettings): ModularPanel {
        syncManager.registerSlotGroup("metal_chest_inv", inventoryRowSize)

        val columnStr = "I".repeat(inventoryColumnSize)
        val matrixStr = (0..<inventoryRowSize).map { columnStr }

        val pageController = PagedWidget.Controller()
        val pagedWidget = PagedWidget()
            .controller(pageController)
        for (pageIndex in 0..<inventoryPage) {
            pagedWidget.addPage(
                SlotGroupWidget.builder()
                    .matrix(*matrixStr.toTypedArray())
                    .key('I') { slotIndex ->
                        ItemSlot().slot(SyncHandlers.itemSlot(itemInventory, slotIndex + (pageIndex * (inventoryRowSize * inventoryColumnSize))))
                    }.build()
            )
        }

        val width = max(max(inventoryColumnSize, 9) * 18 + 14, GUI_DEFAULT_WIDTH + 56)
        val chestInventoryWidth = inventoryColumnSize * 18
        val playerInventoryWidth = 162
        return ModularPanel.defaultPanel("metal_chest_inv", width, 18 + inventoryRowSize * 18 + 94 + 2)
            .child(Column().margin(7).sizeRel(1f)
                .child(ParentWidget().widthRel(1f).expanded().marginBottom(2)
                    .child(IKey.lang("gui.clayium.metal_chest", IKey.lang(material.translationKey)).asWidget()
                        .top(0).left(((width - 7 * 2) - chestInventoryWidth) / 2))
                    .child(pagedWidget.alignX(Alignment.Center)
                        .margin(0, 9).height(18 * inventoryRowSize).width(inventoryColumnSize * 18))
                    .child(IKey.lang("container.inventory").asWidget()
                        .bottom(0).left(((width - 7 * 2) - playerInventoryWidth) / 2)))
                .child(ParentWidget().right(0).bottom(14).width(12 + 2 + 12).height(12 + 4 + 9)
                    .child(ButtonWidget()
                        .onMousePressed {
                            pagedWidget.previousPage()
                            true
                        }
                        .overlay(IKey.str("<").shadow(false))
                        .align(Alignment.TopLeft)
                        .size(12, 12))
                    .child(ButtonWidget()
                        .onMousePressed {
                            pagedWidget.nextPage()
                            true
                        }
                        .overlay(IKey.str(">").shadow(false))
                        .align(Alignment.TopRight)
                        .size(12, 12))
                    .child(IKey.dynamic { "${pagedWidget.currentPageIndex + 1} / $inventoryPage" }.asWidgetResizing()
                        .align(Alignment.BottomCenter))
                )
                .child(MuiSlots.playerInventory(0)))
    }
}