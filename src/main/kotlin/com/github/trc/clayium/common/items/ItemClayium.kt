package com.github.trc.clayium.common.items

import com.github.trc.clayium.api.CValues
import com.github.trc.clayium.api.util.clayiumId
import com.github.trc.clayium.common.Clayium
import net.minecraft.item.Item

/**
 * @param name name without mod id
 */
open class ItemClayium(name: String) : Item() {
    init {
        this.creativeTab = Clayium.creativeTab
        this.registryName = clayiumId(name)
        this.translationKey = "${CValues.MOD_ID}.$name"
    }
}