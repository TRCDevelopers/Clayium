package com.github.trcdevelopers.clayium.api.capability

import com.github.trcdevelopers.clayium.api.metatileentity.MetaTileEntity
import net.minecraft.util.math.BlockPos

interface ISynchronizedInterface {

    val target: MetaTileEntity?

    /**
     * should be synced to the client since it is used for rendering
     */
    val targetPos: BlockPos?

    /**
     * -1 if no target
     * should be synced to the client since it is used for rendering
     */
    val targetDimensionId: Int
}