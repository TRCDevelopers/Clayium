package com.github.trc.clayium.api.capability

import net.minecraft.entity.Entity

interface IItemGadget {
    fun updateInventory(player: Entity, isRemote: Boolean) {}

    fun putInHolder(player: Entity)
    fun removeFromHolder(player: Entity)
}