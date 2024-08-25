package com.github.trc.clayium.common.metatileentities

import com.cleanroommc.modularui.factory.PosGuiData
import com.cleanroommc.modularui.screen.ModularPanel
import com.cleanroommc.modularui.utils.Alignment
import com.cleanroommc.modularui.value.sync.GuiSyncManager
import com.cleanroommc.modularui.value.sync.SyncHandlers
import com.cleanroommc.modularui.widgets.ButtonWidget
import com.cleanroommc.modularui.widgets.ItemSlot
import com.cleanroommc.modularui.widgets.SlotGroupWidget
import com.cleanroommc.modularui.widgets.layout.Grid
import com.github.trc.clayium.api.CValues
import com.github.trc.clayium.api.capability.impl.ClayiumItemStackHandler
import com.github.trc.clayium.api.capability.impl.EmptyItemStackHandler
import com.github.trc.clayium.api.capability.impl.LaserEnergyHolder
import com.github.trc.clayium.api.metatileentity.MetaTileEntity
import com.github.trc.clayium.api.util.ITier
import com.github.trc.clayium.api.util.clayiumId
import com.github.trc.clayium.common.gui.ClayGuiTextures
import com.github.trc.clayium.common.util.TransferUtils
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.util.NonNullList
import net.minecraft.util.ResourceLocation
import net.minecraftforge.items.IItemHandlerModifiable

class BlockBreakerMetaTileEntity(
    metaTileEntityId: ResourceLocation,
    tier: ITier
) : MetaTileEntity(
    metaTileEntityId, tier,
    validInputModes = validInputModesLists[0], validOutputModes = validOutputModesLists[1],
    translationKey = "machine.${CValues.MOD_ID}.block_breaker"
) {
    private val inventoryRow = 3
    private val inventoryColumn = 3

    override val faceTexture: ResourceLocation = clayiumId("blocks/area_miner")

    override val itemInventory = ClayiumItemStackHandler(this, inventoryRow * inventoryColumn)
    override val importItems: IItemHandlerModifiable = EmptyItemStackHandler
    override val exportItems = itemInventory

    private val laserPower: LaserEnergyHolder = LaserEnergyHolder(this)

    override fun createMetaTileEntity(): MetaTileEntity {
        return BlockBreakerMetaTileEntity(metaTileEntityId, tier)
    }

    override fun registerItemModel(item: Item, meta: Int) {
        registerItemModelDefaultNew(item, meta, "block_breaker")
    }

    override fun buildUI(data: PosGuiData, syncManager: GuiSyncManager): ModularPanel {
        syncManager.registerSlotGroup("breaker_inv", inventoryRow)
        val columnStr = "I".repeat(inventoryColumn)
        val matrixStr = (0..<inventoryRow).map { columnStr }

        val startButton = ButtonWidget()
            .background(ClayGuiTextures.START_BUTTON)
            .hoverBackground(ClayGuiTextures.START_BUTTON_HOVERED)
        val stopButton = ButtonWidget()
            .background(ClayGuiTextures.STOP_BUTTON)
            .hoverBackground(ClayGuiTextures.STOP_BUTTON_HOVERED)
        val displayRange = ButtonWidget()
            .background(ClayGuiTextures.DISPLAY_RANGE)
            .hoverBackground(ClayGuiTextures.DISPLAY_RANGE_HOVERED)
        val resetButton = ButtonWidget()
            .background(ClayGuiTextures.RESET)
            .hoverBackground(ClayGuiTextures.RESET_HOVERED)

        return ModularPanel.defaultPanel("breaker", 176, 186)
            .columnWithPlayerInv {
                child(buildMainParentWidget(syncManager)
                    .child(Grid().coverChildren()
                        .row(startButton, stopButton)
                        .row(displayRange, resetButton)
                        .minElementMargin(1, 1)
                        .left(4).top(12)
                    )
                    .child(SlotGroupWidget.builder()
                        .matrix(*matrixStr.toTypedArray())
                        .key('I') { ItemSlot().slot(SyncHandlers.itemSlot(itemInventory, it).slotGroup("breaker_inv")) }
                        .build().alignX(Alignment.TopCenter.x).top(12)
                    )
                    .child(laserPower.createLpTextWidget(syncManager)
                        .alignX(Alignment.Center.x).bottom(12)
                    )
                )
            }
    }

    override fun update() {
        super.update()
        if (!isRemote) {
            val pos = this.pos ?: return
            val world = this.world ?: return
            val frontPos = pos.add(frontFacing.directionVec)
            val blockState = world.getBlockState(frontPos)
            val drops = NonNullList.create<ItemStack>()
            blockState.block.getDrops(drops, world, frontPos, blockState, 0)
            if (TransferUtils.insertToHandler(itemInventory, drops, true)) {
                TransferUtils.insertToHandler(itemInventory, drops, false)
            }
            world.destroyBlock(frontPos, false)
        }
    }

}