package io.github.trcdevelopers.clayium.api.metatileentity.multiblock

import io.github.trcdevelopers.clayium.api.metatileentity.MetaTileEntity
import io.github.trcdevelopers.clayium.api.util.ITier

interface IMultiblockPart {
    val tier: ITier
    val isAttachedToMultiblock: Boolean

    fun addToMultiblock(controller: MetaTileEntity)
    fun removeFromMultiblock(controller: MetaTileEntity)
    fun canPartShare() = true
}