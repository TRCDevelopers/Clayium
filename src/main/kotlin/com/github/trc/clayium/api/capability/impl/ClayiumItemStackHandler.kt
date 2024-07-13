package com.github.trc.clayium.api.capability.impl

import com.github.trc.clayium.api.metatileentity.MetaTileEntity
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