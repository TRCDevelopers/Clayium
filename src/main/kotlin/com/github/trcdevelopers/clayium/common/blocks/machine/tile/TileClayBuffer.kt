package com.github.trcdevelopers.clayium.common.blocks.machine.tile

import com.github.trcdevelopers.clayium.common.Clayium
import com.github.trcdevelopers.clayium.common.GuiHandler
import com.github.trcdevelopers.clayium.common.blocks.machine.MachineIoMode
import com.github.trcdevelopers.clayium.common.config.ConfigTierBalance
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.nbt.NBTTagCompound
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
        player.openGui(Clayium.INSTANCE, GuiHandler.CLAY_BUFFER, world, pos.x, pos.y, pos.z)
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
        this.autoIoHandler = AutoIoHandler(
            ConfigTierBalance.bufferInterval[tier],
            ConfigTierBalance.bufferAmount[tier],
        )
        this.itemStackHandler = object : ItemStackHandler(inventoryX * inventoryY) {
            override fun onContentsChanged(slot: Int) = this@TileClayBuffer.markDirty()
        }
    }

    override fun writeToNBT(compound: NBTTagCompound): NBTTagCompound {
        compound.setTag("inventory", itemStackHandler.serializeNBT())
        return super.writeToNBT(compound)
    }

    override fun readFromNBT(compound: NBTTagCompound) {
        super.readFromNBT(compound)
        itemStackHandler.deserializeNBT(compound.getCompoundTag("inventory"))
    }

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