package com.github.trc.clayium.common.event

import com.github.trc.clayium.api.capability.ClayiumCapabilities
import com.github.trc.clayium.api.capability.ClayiumPlayerData
import com.github.trc.clayium.api.util.clayiumId
import com.github.trc.clayium.common.capability.impl.GadgetRepeatedlyAttack
import com.github.trc.clayium.common.items.ItemClayGadgetHolder
import net.minecraft.entity.Entity
import net.minecraft.entity.player.EntityPlayer
import net.minecraftforge.event.AttachCapabilitiesEvent
import net.minecraftforge.event.entity.EntityJoinWorldEvent
import net.minecraftforge.event.entity.living.LivingAttackEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.gameevent.PlayerEvent
import net.minecraftforge.fml.common.gameevent.TickEvent
import net.minecraftforge.items.CapabilityItemHandler

object EntityEventListener {
    @SubscribeEvent
    fun onPlayerTick(e: TickEvent.PlayerTickEvent) {
        if (e.phase != TickEvent.Phase.START) return

        ItemClayGadgetHolder.onTick(e.player)
    }

    @SubscribeEvent
    fun onEntityJoin(event: EntityJoinWorldEvent) {
        val entity = event.entity
        if (entity is EntityPlayer) {
            ItemClayGadgetHolder.onPlayerLogin(entity)
        }
    }

    @SubscribeEvent
    fun onPlayerLogout(event: PlayerEvent.PlayerLoggedOutEvent) {
        val player = event.player
        ItemClayGadgetHolder.onPlayerLogout(player)
    }

    @SubscribeEvent
    fun onAttachCapabilityEntity(e: AttachCapabilitiesEvent<Entity>) {
        val player = e.`object`
        if (player is EntityPlayer) {
            e.addCapability(clayiumId("player_data"), ClayiumPlayerData())
        }
    }

    @SubscribeEvent
    fun onAttacked(e: LivingAttackEvent) {
        val entity = e.source.immediateSource
            ?: return
        if (entity is EntityPlayer) {
            val victim = e.entityLiving
                ?: return
            for (i in 0..<entity.inventory.sizeInventory) {
                val stack = entity.inventory.getStackInSlot(i)
                if (stack.isEmpty) continue
                if (stack.item is ItemClayGadgetHolder) {
                    val handler = stack.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null)
                        ?: continue
                    for (j in 0..<handler.slots) {
                        val gadgetStack = handler.getStackInSlot(j)
                        if (gadgetStack.isEmpty) continue
                        val gadget = gadgetStack.getCapability(ClayiumCapabilities.CLAY_GADGET, null)
                            ?: continue
                        if (gadget is GadgetRepeatedlyAttack) {
                            victim.hurtResistantTime = 0
                        }
                    }
                }
            }
        }
    }
}