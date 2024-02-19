package com.github.trcdevelopers.clayium.common.items

import com.github.trcdevelopers.clayium.common.Clayium
import net.minecraft.item.Item
import net.minecraft.util.ResourceLocation

/**
 * @param name name without mod id
 */
open class ItemClayium(name: String) : Item() {
    init {
        this.creativeTab = Clayium.creativeTab
        this.registryName = ResourceLocation(Clayium.MOD_ID, name)
        this.translationKey = "${Clayium.MOD_ID}.$name"
    }
}