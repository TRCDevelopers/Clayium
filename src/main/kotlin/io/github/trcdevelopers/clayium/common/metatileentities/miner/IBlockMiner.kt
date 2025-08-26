package io.github.trcdevelopers.clayium.common.metatileentities.miner

import net.minecraft.block.state.IBlockState
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import net.minecraftforge.items.IItemHandler

interface IBlockMiner {
    fun mineBlock(world: World, pos: BlockPos, state: IBlockState, inputInventory: IItemHandler, outputInventory: IItemHandler)
}