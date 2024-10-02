package com.github.trc.clayium.common.advancements.triggers

import net.minecraft.advancements.CriteriaTriggers

object ModTriggers {
    @JvmField
    val INVENTORY_CHANGED_OREDICT = InventoryChangedOreDictTrigger()

    fun registerTriggers() {
        CriteriaTriggers.register(INVENTORY_CHANGED_OREDICT)
    }
}