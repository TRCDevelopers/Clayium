package com.github.trc.clayium.common.advancements.triggers

import com.github.trc.clayium.api.util.clayiumId
import com.github.trc.clayium.common.advancements.ItemPredicateOreDict
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import net.minecraft.advancements.ICriterionTrigger
import net.minecraft.advancements.PlayerAdvancements
import net.minecraft.entity.player.EntityPlayerMP
import net.minecraft.entity.player.InventoryPlayer
import net.minecraft.util.ResourceLocation

class InventoryChangedOreDictTrigger : ICriterionTrigger<InventoryChangedOreDictInstance> {
    private val listenersMap: MutableMap<PlayerAdvancements, Listeners> = mutableMapOf()
    override fun getId(): ResourceLocation {
        return ID
    }

    override fun addListener(playerAdvancementsIn: PlayerAdvancements, listener: ICriterionTrigger.Listener<InventoryChangedOreDictInstance>) {
        listenersMap.computeIfAbsent(playerAdvancementsIn) { Listeners(playerAdvancementsIn) }
            .add(listener)

    }

    override fun removeListener(playerAdvancementsIn: PlayerAdvancements, listener: ICriterionTrigger.Listener<InventoryChangedOreDictInstance>) {
        val listeners = listenersMap[playerAdvancementsIn]
        if (listeners != null) {
            listeners.remove(listener)
            if (listeners.isEmpty) {
                this.listenersMap.remove(playerAdvancementsIn)
            }
        }
    }

    override fun removeAllListeners(playerAdvancementsIn: PlayerAdvancements) {
        this.listenersMap.remove(playerAdvancementsIn)
    }

    override fun deserializeInstance(json: JsonObject, context: JsonDeserializationContext): InventoryChangedOreDictInstance {
        val items: JsonElement? = json.get("items")
        if (items == null || items.isJsonNull) {
            throw IllegalArgumentException("clayium:inventory_changed_oredict requires 'items' to be present")
        }
        return InventoryChangedOreDictInstance(ItemPredicateOreDict.deserializeArray(items).toMutableList())
    }

    fun trigger(player: EntityPlayerMP, inventory: InventoryPlayer) {
        listenersMap[player.advancements]?.trigger(inventory)
    }

    class Listeners(private val playerAdvancements: PlayerAdvancements) {
        private val set: MutableSet<ICriterionTrigger.Listener<InventoryChangedOreDictInstance>> = mutableSetOf()

        val isEmpty: Boolean get() = set.isEmpty()
        fun add(listener: ICriterionTrigger.Listener<InventoryChangedOreDictInstance>) = set.add(listener)
        fun remove(listener: ICriterionTrigger.Listener<InventoryChangedOreDictInstance>) = set.remove(listener)

        fun trigger(inventory: InventoryPlayer) {
            var list: MutableList<ICriterionTrigger.Listener<InventoryChangedOreDictInstance>>? = null
            for (listener in this.set) {
                val instance = listener.criterionInstance
                if (instance.test(inventory)) {
                    if (list == null) { list = mutableListOf() }
                    list.add(listener)
                }
            }

            if (list != null) {
                for (listener in list) {
                    listener.grantCriterion(this.playerAdvancements)
                }
            }
        }
    }

    companion object {
        val ID = clayiumId("inventory_changed_oredict")
    }
}