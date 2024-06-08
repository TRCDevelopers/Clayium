package com.github.trcdevelopers.clayium.api.capability.impl

import com.github.trcdevelopers.clayium.api.metatileentity.MetaTileEntity
import net.minecraftforge.items.ItemStackHandler

open class ClayiumItemStackHandler(
    val metaTileEntity: MetaTileEntity,
    size: Int,
) : ItemStackHandler(size) {
    override fun onContentsChanged(slot: Int) {
        super.onContentsChanged(slot)
        metaTileEntity.markDirty()
    }
}