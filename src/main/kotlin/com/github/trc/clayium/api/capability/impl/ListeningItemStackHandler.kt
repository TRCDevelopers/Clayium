package com.github.trc.clayium.api.capability.impl

import net.minecraftforge.items.ItemStackHandler

class ListeningItemStackHandler(size: Int, private val listener: (Int) -> Unit) :
    ItemStackHandler(size) {
    override fun onContentsChanged(slot: Int) {
        listener(slot)
    }
}
