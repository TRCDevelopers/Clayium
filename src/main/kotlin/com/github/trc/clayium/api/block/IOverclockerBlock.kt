package com.github.trc.clayium.api.block

import net.minecraft.util.math.BlockPos
import net.minecraft.world.IBlockAccess

interface IOverclockerBlock {
    fun getOverclockFactor(world: IBlockAccess, pos: BlockPos): Double
}