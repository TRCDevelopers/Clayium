package com.github.trc.clayium.api.capability.impl

import com.github.trc.clayium.api.metatileentity.interfaces.IMarkDirty
import net.minecraftforge.items.ItemStackHandler

open class ClayiumItemStackHandler(
    val notifiable: IMarkDirty,
    size: Int,
) : ItemStackHandler(size) {
    override fun onContentsChanged(slot: Int) {
        super.onContentsChanged(slot)
        notifiable.markDirty()
    }
}
