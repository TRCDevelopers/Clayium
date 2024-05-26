package com.github.trcdevelopers.clayium.api.capability

import com.github.trcdevelopers.clayium.api.metatileentity.MetaTileEntity
import net.minecraft.util.math.BlockPos

interface ISynchronizedInterface {
    val target: MetaTileEntity?
    val targetPos: BlockPos?
    /**
     * -1 if no target
     */
    val targetDimensionId: Int
}