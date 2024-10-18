package com.github.trc.clayium.api.block

import net.minecraft.block.state.IBlockState

interface IResonatingBlock {
    fun getResonance(state: IBlockState): Double
}
