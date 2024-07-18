package com.github.trc.clayium.common.metatileentity

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
import com.github.trc.clayium.api.capability.impl.EmptyItemStackHandler
import com.github.trc.clayium.api.capability.impl.LaserPowerHolder
import com.github.trc.clayium.api.metatileentity.MetaTileEntity
import com.github.trc.clayium.api.util.ITier
import com.github.trc.clayium.api.util.clayiumId
import net.minecraft.client.renderer.block.model.ModelResourceLocation
import net.minecraft.item.Item
import net.minecraft.util.EnumFacing
import net.minecraft.util.ResourceLocation
import net.minecraftforge.client.model.ModelLoader
import net.minecraftforge.common.capabilities.Capability
import net.minecraftforge.items.IItemHandlerModifiable

class BlockBreakerMetaTileEntity(
    metaTileEntityId: ResourceLocation,
    tier: ITier
) : MetaTileEntity(
    metaTileEntityId,
    tier,
    validInputModes = validInputModesLists[0],
    validOutputModesLists[1],
    translationKey = "machine.${CValues.MOD_ID}.block_breaker"
) {
    private val inventoryRow = 3
    private val inventoryColumn = 3
    var laserPower: LaserPowerHolder = LaserPowerHolder(this)
    override val faceTexture: ResourceLocation = clayiumId("blocks/block_breaker")
    override val itemInventory = ClayiumItemStackHandler(this, inventoryRow*inventoryColumn)
    override val importItems: IItemHandlerModifiable = EmptyItemStackHandler
    override val exportItems = itemInventory

    override fun createMetaTileEntity(): MetaTileEntity {
        return BlockBreakerMetaTileEntity(metaTileEntityId, tier)
    }

    override fun <T> getCapability(capability: Capability<T>, facing: EnumFacing?): T? {

        return super.getCapability(capability, facing)
    }

    override fun registerItemModel(item: Item, meta: Int) {
        ModelLoader.setCustomModelResourceLocation(item, meta, ModelResourceLocation(clayiumId("clay_multi_track_buffer"), "tier=${tier.numeric}"))
    }

    override fun buildUI(
        data: PosGuiData?,
        syncManager: GuiSyncManager
    ): ModularPanel? {
        syncManager.registerSlotGroup("breaker_inv", inventoryRow)
        val columnStr = "I".repeat(inventoryColumn)
        val matrixStr = (0..<inventoryRow).map { columnStr }
        return ModularPanel("breaker")
            .flex {
                it.size(176,  18 + inventoryRow * 18 + 120 + 2)
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
                                .slotGroup("breaker_inv")
                        )
                    }
                    .build())
                .child(
                    laserPower.createLpTextWidget(syncManager)
                        .paddingTop(1)
                        .paddingBottom(1)
                        .anchor(Alignment.Center)
                )
                .child(
                    TextWidget(IKey.lang("container.inventory"))
                        .paddingTop(1)
                        .paddingBottom(1)
                        .left(6)))
            .bindPlayerInventory()

    }


    override fun update() {
        if (!isRemote) {
            if(world!=null && this.getNeighbor(frontFacing)!=null)
                world!!.destroyBlock(this.getNeighbor(frontFacing)!!.pos,true)
        }
        super.update()
    }

}