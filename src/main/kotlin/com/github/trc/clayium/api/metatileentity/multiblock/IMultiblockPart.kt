package com.github.trc.clayium.api.metatileentity.multiblock

import com.github.trc.clayium.api.util.ITier

interface IMultiblockPart {
    val tier: ITier
    val isAttachedToMultiblock: Boolean

    fun addToMultiblock(controller: MultiblockControllerBase)
    fun removeFromMultiblock(controller: MultiblockControllerBase)
    fun canPartShare() = true
}