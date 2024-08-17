package com.github.trc.clayium.api.metatileentity

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
import com.github.trc.clayium.api.CValues
import com.github.trc.clayium.api.capability.impl.ClayiumItemStackHandler
import com.github.trc.clayium.api.util.ITier
import com.github.trc.clayium.api.util.clayiumId
import net.minecraft.client.renderer.block.model.ModelResourceLocation
import net.minecraft.item.Item
import net.minecraft.util.EnumFacing
import net.minecraft.util.ResourceLocation
import net.minecraftforge.client.model.ModelLoader
import net.minecraftforge.common.capabilities.Capability
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import net.minecraftforge.items.CapabilityItemHandler

class ClayBufferMetaTileEntity(
    metaTileEntityId: ResourceLocation,
    tier: ITier,
) : MetaTileEntity(metaTileEntityId, tier,
    validInputModes = bufferValidInputModes, validOutputModes = validOutputModesLists[1], "machine.${CValues.MOD_ID}.clay_buffer") {

    override val hasFrontFacing: Boolean = false

    val inventoryRowSize = when (tier.numeric) {
        in 4..7 -> tier.numeric - 3
        8, -> 4
        in 9..13 -> 6
        else -> 1
    }
    val inventoryColumnSize = when (tier.numeric) {
        in 4..7 -> tier.numeric - 2
        in 8..13 -> 9
        else -> 1
    }

    override val itemInventory = ClayiumItemStackHandler(this, inventoryRowSize * inventoryColumnSize)
    override val importItems = itemInventory
    override val exportItems = itemInventory
    val autoIoHandler: AutoIoHandler = AutoIoHandler.Combined(this, isBuffer = true)

    override fun createMetaTileEntity(): MetaTileEntity {
        return ClayBufferMetaTileEntity(this.metaTileEntityId, this.tier)
    }

    override fun <T> getCapability(capability: Capability<T>, facing: EnumFacing?): T? {
        if (capability === CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            return CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.cast(
                createFilteredItemHandler(itemInventory, facing)
            )
        }
        return super.getCapability(capability, facing)
    }

    override fun isFacingValid(facing: EnumFacing): Boolean {
        return true
    }

    override fun canImportFrom(side: EnumFacing) = true
    override fun canExportTo(side: EnumFacing) = true

    override fun onPlacement() {
        this.toggleInput(this.frontFacing.opposite)
        super.onPlacement()
    }

    override fun buildUI(data: PosGuiData, syncManager: GuiSyncManager): ModularPanel {
        syncManager.registerSlotGroup("buffer_inv", inventoryRowSize)
        val columnStr = "I".repeat(inventoryColumnSize)
        val matrixStr = (0..<inventoryRowSize).map { columnStr }

        return ModularPanel("clay_buffer")
            .flex {
                it.size(176,  18 + inventoryRowSize * 18 + 94 + 2)
                it.align(Alignment.Center)
            }
            .child(
                TextWidget(IKey.lang(this.translationKey, IKey.lang(tier.prefixTranslationKey)))
                    .margin(6)
                    .align(Alignment.TopLeft))
            .child(Column()
                .marginTop(18)
                .child(SlotGroupWidget.builder()
                    .matrix(*matrixStr.toTypedArray())
                    .key('I') { index ->
                        ItemSlot().slot(
                            SyncHandlers.itemSlot(itemInventory, index)
                                .slotGroup("buffer_inv")
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

    @SideOnly(Side.CLIENT)
    override fun registerItemModel(item: Item, meta: Int) {
        ModelLoader.setCustomModelResourceLocation(item, meta, ModelResourceLocation(clayiumId("clay_buffer"), "tier=${tier.numeric}"))
    }
}