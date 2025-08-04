package com.github.trc.clayium.api.capability

import net.minecraft.entity.player.EntityPlayer
import net.minecraft.util.ResourceLocation

/**
 * Capability interface for clay gadgets.
 * Items that have this capability can be put in the gadget holder.
 *
 * You have to use unique singleton instance for the capability,
 * or override the [equals] and [hashCode] because Sets and Maps are used in logic.
 * See the companion object of [com.github.trc.clayium.common.items.ItemClayGadgetHolder] for details.
 */
interface IItemGadget {
    /**
     * If gadgets have the same category, they can't be used together.
     * i.e, you can't put two gadgets of the same category to the gadgets holder at the same time.
     */
    val category: ResourceLocation

    /**
     * Called every tick when the gadget is in the holder.
     */
    fun updateInventory(player: EntityPlayer, isRemote: Boolean) {}

    /**
     * Called on [net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent].
     */
    fun onLogin(player: EntityPlayer) {}

    /**
     * Called on [net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedOutEvent].
     */
    fun onLogout(player: EntityPlayer) {}

    /**
     * Called when the gadget is put in the holder.
     */
    fun putInHolder(player: EntityPlayer) {}

    /**
     * Called when the gadget is taken out of the holder.
     */
    fun removeFromHolder(player: EntityPlayer) {}
}