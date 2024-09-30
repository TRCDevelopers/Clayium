package com.github.trc.clayium.common.items

import com.github.trc.clayium.api.MOD_ID
import com.github.trc.clayium.api.util.clayiumId
import com.github.trc.clayium.common.creativetab.ClayiumCTabs
import net.minecraft.item.Item

/**
 * @param name name without mod id
 */
open class ItemClayium(name: String) : Item() {
    init {
        this.creativeTab = ClayiumCTabs.main
        this.registryName = clayiumId(name)
        this.translationKey = "${MOD_ID}.$name"
    }
}