package com.github.trc.clayium.api.capability

import com.github.trc.clayium.api.metatileentity.MetaTileEntity
import net.minecraft.util.math.BlockPos

interface ISynchronizedInterface {

    val target: MetaTileEntity?

    /**
     * should be synced to the client since it is used for rendering.
     * null if not target.
     */
    val targetPos: BlockPos?

    /**
     * should be synced to the client since it is used for rendering
     * -1 if not target.
     */
    val targetDimensionId: Int

    /**
     * synchronize this block to given pos of given dimension.
     *
     * @return true if the synchronization was successful
     */
    fun synchronize(pos: BlockPos, dimensionId: Int): Boolean
}