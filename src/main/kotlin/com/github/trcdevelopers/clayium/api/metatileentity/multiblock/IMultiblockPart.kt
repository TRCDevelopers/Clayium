package com.github.trcdevelopers.clayium.api.metatileentity.multiblock

import com.github.trcdevelopers.clayium.api.util.ITier

interface IMultiblockPart {
    val tier: ITier

    fun isAttachedToMultiblock(): Boolean
    fun addToMultiblock(controller: MultiblockControllerBase)
    fun removeFromMultiblock(controller: MultiblockControllerBase)
    fun canPartShare() = true
}