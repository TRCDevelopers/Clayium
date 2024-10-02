package com.github.trc.clayium.common.advancements.triggers

import com.github.trc.clayium.common.advancements.ItemPredicateOreDict
import net.minecraft.advancements.ICriterionInstance
import net.minecraft.entity.player.InventoryPlayer
import net.minecraft.util.ResourceLocation

class InventoryChangedOreDictInstance(private val items: MutableList<ItemPredicateOreDict>) : ICriterionInstance {
    override fun getId(): ResourceLocation {
        return InventoryChangedOreDictTrigger.ID
    }

    fun test(inventory: InventoryPlayer): Boolean {
        for (i in 0..<inventory.sizeInventory) {
            val stack = inventory.getStackInSlot(i)
            if (stack.isEmpty) { continue }
            val iterator = items.iterator()
            while (iterator.hasNext()) {
                val itemPredicate = iterator.next()
                if (itemPredicate.test(stack)) {
                    iterator.remove()
                }
            }
            return items.isEmpty()
        }
        return false
    }
}