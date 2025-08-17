package io.github.trcdevelopers.clayium.api.block

import net.minecraft.util.math.BlockPos
import net.minecraft.world.IBlockAccess

/**
 * Interface for blocks.
 * Implement this interface to create Energy Storage Upgrade blocks.
 */
interface IEnergyStorageUpgradeBlock {
    /**
     * **additional** stack size limit.
     */
    fun getExtraStackLimit(world: IBlockAccess, pos: BlockPos): Int
}