package com.github.trc.clayium.api.block

import net.minecraft.util.math.BlockPos
import net.minecraft.world.IBlockAccess

interface IEnergyStorageUpgradeBlock {
    fun getMultiplier(world: IBlockAccess, pos: BlockPos): Int
}