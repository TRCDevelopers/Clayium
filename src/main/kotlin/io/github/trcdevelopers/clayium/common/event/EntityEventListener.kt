package io.github.trcdevelopers.clayium.common.event

import io.github.trcdevelopers.clayium.api.capability.ClayiumPlayerData
import io.github.trcdevelopers.clayium.api.util.clayiumId
import io.github.trcdevelopers.clayium.common.capability.impl.GadgetRepeatedlyAttack
import io.github.trcdevelopers.clayium.common.items.ItemClayGadgetHolder
import net.minecraft.entity.Entity
import net.minecraft.entity.player.EntityPlayer
import net.minecraftforge.event.AttachCapabilitiesEvent
import net.minecraftforge.event.entity.EntityJoinWorldEvent
import net.minecraftforge.event.entity.living.LivingAttackEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.gameevent.PlayerEvent
import net.minecraftforge.fml.common.gameevent.TickEvent

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
            if (ItemClayGadgetHolder.hasGadget(entity.uniqueID, GadgetRepeatedlyAttack)) {
                victim.hurtResistantTime = 0
            }
        }
    }
}