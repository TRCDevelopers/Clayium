package com.github.trcdevelopers.clayium.api.metatileentity.multiblock

import com.github.trcdevelopers.clayium.api.metatileentity.MultiblockControllerBase

interface IMultiblockPart {
    fun isAttachedToMultiblock(): Boolean
    fun addToMultiblock(controller: MultiblockControllerBase)
    fun removeFromMultiblock(controller: MultiblockControllerBase)
    fun canPartShare() = true
}