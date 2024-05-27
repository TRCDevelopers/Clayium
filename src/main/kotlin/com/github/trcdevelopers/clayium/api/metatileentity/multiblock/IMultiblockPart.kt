package com.github.trcdevelopers.clayium.api.metatileentity.multiblock

interface IMultiblockPart {
    val tier: Int

    fun isAttachedToMultiblock(): Boolean
    fun addToMultiblock(controller: MultiblockControllerBase)
    fun removeFromMultiblock(controller: MultiblockControllerBase)
    fun canPartShare() = true
}