package com.github.trcdevelopers.clayium.common.blocks.machine.tile

import com.github.trcdevelopers.clayium.common.Clayium
import com.github.trcdevelopers.clayium.common.GuiHandler
import com.github.trcdevelopers.clayium.common.blocks.machine.MachineIoMode
import com.github.trcdevelopers.clayium.common.config.ConfigTierBalance
import com.github.trcdevelopers.clayium.common.items.ItemClayConfigTool
import net.minecraft.entity.EntityLivingBase
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.EnumFacing
import net.minecraft.util.EnumHand
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import net.minecraftforge.common.capabilities.Capability
import net.minecraftforge.items.CapabilityItemHandler
import net.minecraftforge.items.IItemHandler
import net.minecraftforge.items.ItemStackHandler

class TileClayBuffer : TileMachine() {

    override lateinit var autoIoHandler: AutoIoHandler

    private lateinit var itemStackHandler: ItemStackHandler

    var inventoryY: Int = 1
        private set
    var inventoryX: Int = 1
        private set

    override fun openGui(player: EntityPlayer, world: World, pos: BlockPos) {
        player.openGui(Clayium, GuiHandler.CLAY_BUFFER, world, pos.x, pos.y, pos.z)
    }

    override fun getItemHandler(): IItemHandler {
        return itemStackHandler
    }

    override fun initParams(tier: Int, inputModes: List<MachineIoMode>, outputModes: List<MachineIoMode>) {
        super.initParams(tier, inputModes, outputModes)
        this.inventoryY = when (tier) {
            in 4..7 -> tier - 3
            8, -> 4
            in 9..13 -> 6
            else -> 1
        }
        this.inventoryX = when (tier) {
            in 4..7 -> tier - 2
            in 8..13 -> 9
            else -> 1
        }
        this.itemStackHandler = object : ItemStackHandler(inventoryX * inventoryY) {
            override fun onContentsChanged(slot: Int) = this@TileClayBuffer.markDirty()
        }
        this.autoIoHandler = AutoIoHandler(
            ConfigTierBalance.bufferInterval[tier],
            ConfigTierBalance.bufferAmount[tier],
        )
    }

    override fun onBlockPlaced(player: EntityLivingBase, stack: ItemStack) {
        super.onBlockPlaced(player, stack)
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

    override fun acceptInputFrom(side: EnumFacing) = true
    override fun acceptOutputTo(side: EnumFacing) = true
    override fun isAutoInput(side: EnumFacing) = _inputs[side.index] == MachineIoMode.ALL
    override fun isAutoOutput(side: EnumFacing) = _outputs[side.index] == MachineIoMode.ALL

    override fun canAutoInput(side: EnumFacing) = _inputs[side.index] == MachineIoMode.ALL
    override fun canAutoOutput(side: EnumFacing) = _outputs[side.index] == MachineIoMode.ALL

    companion object {
        @JvmStatic
        private val ITEM_HANDLER_CAPABILITY: Capability<IItemHandler> = CapabilityItemHandler.ITEM_HANDLER_CAPABILITY

        fun create(tier: Int): TileClayBuffer {
            return TileClayBuffer().apply {
                initParams(tier, MachineIoMode.Input.BUFFER, MachineIoMode.Output.BUFFER)
            }
        }
    }
}