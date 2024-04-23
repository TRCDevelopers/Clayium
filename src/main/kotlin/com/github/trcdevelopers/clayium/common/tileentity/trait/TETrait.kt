package com.github.trcdevelopers.clayium.common.tileentity.trait

import com.github.trcdevelopers.clayium.common.tileentity.TileEntityMachine

abstract class TETrait(
    protected val tileEntity: TileEntityMachine,
    protected val tier: Int,
) {

    open fun update() {}

}