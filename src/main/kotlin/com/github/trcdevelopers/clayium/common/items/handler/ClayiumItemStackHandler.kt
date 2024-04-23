package com.github.trcdevelopers.clayium.common.items.handler

import com.github.trcdevelopers.clayium.common.tileentity.TileEntityMachine
import net.minecraftforge.items.ItemStackHandler

open class ClayiumItemStackHandler(
    private val tileEntity: TileEntityMachine,
    size: Int,
) : ItemStackHandler(size) {
    override fun onContentsChanged(slot: Int) {
        super.onContentsChanged(slot)
        tileEntity.markDirty()
    }
}