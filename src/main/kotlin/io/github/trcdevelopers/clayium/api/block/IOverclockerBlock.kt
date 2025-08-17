package io.github.trcdevelopers.clayium.api.block

import net.minecraft.util.math.BlockPos
import net.minecraft.world.IBlockAccess

/**
 * Interface for blocks.
 * Implement this interface to create Overclocker blocks.
 */
interface IOverclockerBlock {
    fun getOverclockFactor(world: IBlockAccess, pos: BlockPos): Double
}