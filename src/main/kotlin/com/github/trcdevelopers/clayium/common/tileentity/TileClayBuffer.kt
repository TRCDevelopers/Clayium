package com.github.trcdevelopers.clayium.common.tileentity

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
import com.github.trcdevelopers.clayium.common.blocks.machine.MachineIoMode
import com.github.trcdevelopers.clayium.common.items.ItemClayConfigTool
import net.minecraft.entity.EntityLivingBase
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.EnumFacing
import net.minecraft.util.EnumHand
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import net.minecraftforge.common.capabilities.Capability
import net.minecraftforge.items.CapabilityItemHandler
import net.minecraftforge.items.IItemHandler
import net.minecraftforge.items.IItemHandlerModifiable
import net.minecraftforge.items.ItemStackHandler
import kotlin.apply
import kotlin.collections.map
import kotlin.collections.toList
import kotlin.collections.toTypedArray
import kotlin.text.repeat
import kotlin.text.single

class TileClayBuffer : TileEntityMachine(), IGuiHolder<PosGuiData> {


    override lateinit var autoIoHandler: AutoIoHandler

    private lateinit var itemStackHandler: ItemStackHandler
    override lateinit var inputInventory: IItemHandlerModifiable
    override lateinit var outputInventory: IItemHandlerModifiable
    override lateinit var combinedInventory: IItemHandler

    var inventoryRowSize: Int = 1
        private set
    var inventoryColumnSize: Int = 1
        private set

    override fun initializeByTier(tier: Int) {
        super.initializeByTier(tier)
        this.inventoryRowSize = when (tier) {
            in 4..7 -> tier - 3
            8, -> 4
            in 9..13 -> 6
            else -> 1
        }
        this.inventoryColumnSize = when (tier) {
            in 4..7 -> tier - 2
            in 8..13 -> 9
            else -> 1
        }
        this.itemStackHandler = object : ItemStackHandler(inventoryColumnSize * inventoryRowSize) {
            override fun onContentsChanged(slot: Int) = this@TileClayBuffer.markDirty()
        }
        this.autoIoHandler = AutoIoHandler(this, true)
    }

    override fun onBlockPlacedBy(player: EntityLivingBase) {
        super.onBlockPlacedBy(player)
        toggleInput(EnumFacing.getDirectionFromEntityLiving(pos, player).opposite)
    }

    override fun hasCapability(capability: Capability<*>, facing: EnumFacing?): Boolean {
        return capability === ITEM_HANDLER_CAPABILITY || super.hasCapability(capability, facing)
    }

    override fun <T> getCapability(capability: Capability<T>, facing: EnumFacing?): T? {
        if (capability === ITEM_HANDLER_CAPABILITY) {
            return ITEM_HANDLER_CAPABILITY.cast(itemStackHandler)
        }
        return super.getCapability(capability, facing)
    }

    override fun writeToNBT(compound: NBTTagCompound): NBTTagCompound {
        compound.setTag("inventory", itemStackHandler.serializeNBT())
        return super.writeToNBT(compound)
    }

    override fun readFromNBT(compound: NBTTagCompound) {
        super.readFromNBT(compound)
        itemStackHandler.deserializeNBT(compound.getCompoundTag("inventory"))
    }

    override fun onRightClicked(toolType: ItemClayConfigTool.ToolType, worldIn: World, posIn: BlockPos, player: EntityPlayer, hand: EnumHand, clickedSide: EnumFacing, hitX: Float, hitY: Float, hitZ: Float) {
        if (!worldIn.isRemote && toolType == ItemClayConfigTool.ToolType.ROTATION && clickedSide.axis == EnumFacing.Axis.Y) {
            val oldInputs = _inputs.toList()
            val oldOutputs = _outputs.toList()
            for (side in EnumFacing.entries) {
                val rotatedSide = side.rotateAround(EnumFacing.Axis.X)
                _inputs[rotatedSide.index] = oldInputs[side.index]
                _outputs[rotatedSide.index] = oldOutputs[side.index]
            }
            worldIn.notifyBlockUpdate(posIn, worldIn.getBlockState(posIn), worldIn.getBlockState(posIn), 3)
        } else {
            super.onRightClicked(toolType, worldIn, posIn, player, hand, clickedSide, hitX, hitY, hitZ)
        }
    }

    override fun buildUI(data: PosGuiData, syncManager: GuiSyncManager): ModularPanel {
        syncManager.registerSlotGroup("buffer_inv", inventoryRowSize)
        val columnStr = "I".repeat(inventoryColumnSize)
        val matrixStr = (0..<inventoryRowSize).map { columnStr }

        val panel = ModularPanel("clay_buffer")
        panel.flex()
            .size(176,  18 + inventoryRowSize * 18 + 94 + 2)
            .align(Alignment.Center)
        panel
            .child(
                TextWidget(IKey.lang("tile.clayium.clay_buffer", IKey.lang("machine.clayium.tier$tier")))
                .margin(6)
                .align(Alignment.TopLeft))
            .child(Column()
                .marginTop(18)
                .child(SlotGroupWidget.builder()
                    .matrix(*matrixStr.toTypedArray())
                    .key("I".single()) { index ->
                        ItemSlot().slot(
                            SyncHandlers.itemSlot(itemStackHandler, index)
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
        return panel
    }

    companion object {
        @JvmStatic
        private val ITEM_HANDLER_CAPABILITY: Capability<IItemHandler> = CapabilityItemHandler.ITEM_HANDLER_CAPABILITY

        fun create(tier: Int): TileClayBuffer {
            return TileClayBuffer().apply {
                initializeByTier(tier)
                initValidIoModes(MachineIoMode.Input.BUFFER, MachineIoMode.Output.BUFFER)
            }
        }
    }
}