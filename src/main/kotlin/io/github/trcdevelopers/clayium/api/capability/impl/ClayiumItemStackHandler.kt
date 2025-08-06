package io.github.trcdevelopers.clayium.api.capability.impl

import io.github.trcdevelopers.clayium.api.metatileentity.interfaces.IMarkDirty
import net.minecraftforge.items.ItemStackHandler

open class ClayiumItemStackHandler(
    val notifiable: IMarkDirty,
    size: Int,
) : ItemStackHandler(size) {
    override fun onContentsChanged(slot: Int) {
        super.onContentsChanged(slot)
        notifiable.markAsDirty()
    }
}