package io.github.trcdevelopers.clayium.api.block

import net.minecraft.util.math.BlockPos
import net.minecraft.world.IBlockAccess

/**
 * An interface for blocks.
 * Implementing this interface allows the block to be recognized as a reactor hull.
 */
interface ICaReactorHull {
    fun getCaRank(world: IBlockAccess, pos: BlockPos): Int
}