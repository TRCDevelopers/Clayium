package com.github.trc.clayium.api.capability

import net.minecraft.entity.Entity
import net.minecraft.util.ResourceLocation

interface IItemGadget {
    /**
     * If gadgets have the same category, they can't be used together.
     * i.e, you can't put two gadgets of the same category to the gadgets holder at the same time.
     */
    val category: ResourceLocation

    fun updateInventory(player: Entity, isRemote: Boolean) {}

    fun putInHolder(player: Entity)
    fun removeFromHolder(player: Entity)
}