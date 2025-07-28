package com.github.trc.clayium.api.capability

import net.minecraft.entity.Entity
import net.minecraft.item.ItemStack

interface IItemGadget {
    fun updateInventory(inventory: List<ItemStack>, player: Entity, isRemote: Boolean) {}

    fun putInHolder(player: Entity, gadgetStack: ItemStack)
    fun removeFromHolder(player: Entity, gadgetStack: ItemStack)
}