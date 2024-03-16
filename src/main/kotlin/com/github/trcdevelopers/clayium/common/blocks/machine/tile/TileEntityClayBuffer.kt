package com.github.trcdevelopers.clayium.common.blocks.machine.tile

import com.github.trcdevelopers.clayium.common.Clayium
import com.github.trcdevelopers.clayium.common.GuiHandler
import com.github.trcdevelopers.clayium.common.blocks.machine.MachineIoMode
import com.github.trcdevelopers.clayium.common.config.ConfigTierBalance
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import net.minecraftforge.common.capabilities.Capability
import net.minecraftforge.items.CapabilityItemHandler
import net.minecraftforge.items.IItemHandler
import net.minecraftforge.items.ItemStackHandler

class TileEntityClayBuffer : TileMachine() {

    override lateinit var itemStackHandler: IItemHandler
    override lateinit var autoIoHandler: AutoIoHandler

    var inventoryY: Int = 1
        private set
    var inventoryX: Int = 1
        private set

    override fun openGui(player: EntityPlayer, world: World, pos: BlockPos) {
        player.openGui(Clayium.INSTANCE, GuiHandler.CLAY_BUFFER, world, pos.x, pos.y, pos.z)
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
            ConfigTierBalance.bufferTransferIntervals[tier - 1],
            ConfigTierBalance.bufferTransferAmount[tier - 1],
        )
        this.itemStackHandler = object : ItemStackHandler(inventoryX * inventoryY) {
            override fun onContentsChanged(slot: Int) = this@TileEntityClayBuffer.markDirty()
        }
    }

    companion object {
        @JvmStatic
        private val ITEM_HANDLER_CAPABILITY: Capability<IItemHandler> = CapabilityItemHandler.ITEM_HANDLER_CAPABILITY

        fun create(tier: Int): TileEntityClayBuffer {
            return TileEntityClayBuffer().apply {
                initParams(tier, MachineIoMode.Input.BUFFER, MachineIoMode.Output.BUFFER)
            }
        }
    }
}