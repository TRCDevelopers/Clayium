package com.github.trcdevelopers.clayium.common.blocks.machine.tile

import com.github.trcdevelopers.clayium.common.blocks.machine.MachineIoMode
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import net.minecraftforge.items.IItemHandler

/**
 * single input with single output
 *
 * todo: add recipe support, implement things
 */
class TileSimpleMachine : TileMachine() {

    override lateinit var autoIoHandler: AutoIoHandler
    override lateinit var itemStackHandler: IItemHandler

    override fun openGui(player: EntityPlayer, world: World, pos: BlockPos) {
        TODO("Not yet implemented")
    }

    override fun initParams(tier: Int, inputModes: List<MachineIoMode>, outputModes: List<MachineIoMode>) {
        super.initParams(tier, inputModes, outputModes)
        TODO()
    }

    companion object {
        fun create(tier: Int): TileSimpleMachine {
            return TileSimpleMachine().apply {
                initParams(tier, MachineIoMode.Input.SINGLE, MachineIoMode.Output.SINGLE)
            }
        }
    }
}