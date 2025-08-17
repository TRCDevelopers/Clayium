package io.github.trcdevelopers.clayium.api.block

import net.minecraft.block.state.IBlockState

/**
 * Interface for blocks.
 * Implement this interface to create Resonator blocks.
 */
interface IResonatingBlock {
    fun getResonance(state: IBlockState): Double
}