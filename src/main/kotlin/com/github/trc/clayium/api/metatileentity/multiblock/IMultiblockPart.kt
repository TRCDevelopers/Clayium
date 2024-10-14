package com.github.trc.clayium.api.metatileentity.multiblock

import com.github.trc.clayium.api.metatileentity.MetaTileEntity
import com.github.trc.clayium.api.util.ITier

interface IMultiblockPart {
    val tier: ITier
    val isAttachedToMultiblock: Boolean

    fun addToMultiblock(controller: MetaTileEntity)

    fun removeFromMultiblock(controller: MetaTileEntity)

    fun canPartShare() = true
}
