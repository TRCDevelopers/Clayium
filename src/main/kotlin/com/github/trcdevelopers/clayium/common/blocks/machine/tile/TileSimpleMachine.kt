package com.github.trcdevelopers.clayium.common.blocks.machine.tile

import com.github.trcdevelopers.clayium.common.blocks.machine.MachineIoMode
import com.github.trcdevelopers.clayium.common.config.ConfigTierBalance
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import net.minecraftforge.items.IItemHandler
import net.minecraftforge.items.ItemStackHandler

/**
 * single input with single output
 *
 * todo: add recipe support, implement things
 */
class TileSimpleMachine : TileMachine() {

    override lateinit var autoIoHandler: AutoIoHandler

    private lateinit var itemStackHandler: ItemStackHandler

    override fun openGui(player: EntityPlayer, world: World, pos: BlockPos) {
    }

    override fun getItemHandler(): IItemHandler {
        return itemStackHandler
    }

    override fun initParams(tier: Int, inputModes: List<MachineIoMode>, outputModes: List<MachineIoMode>) {
        super.initParams(tier, inputModes, outputModes)
        //todo: temp implementation
        itemStackHandler = ItemStackHandler(1)
        autoIoHandler = AutoIoHandler(
            ConfigTierBalance.machineInterval[tier],
            ConfigTierBalance.machineAmount[tier],
        )
    }

    companion object {
        fun create(tier: Int): TileSimpleMachine {
            return TileSimpleMachine().apply {
                initParams(tier, MachineIoMode.Input.SINGLE, MachineIoMode.Output.SINGLE)
            }
        }
    }
}