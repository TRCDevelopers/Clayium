package com.github.trc.clayium.common.blocks

import com.github.trc.clayium.api.CValues
import com.github.trc.clayium.client.renderer.LaserReflectorItemStackRenderer
import net.minecraft.item.ItemBlock

class ItemBlockClayLaserReflector(block: BlockClayLaserReflector) : ItemBlock(block) {
    init {
        if (CValues.isClient) {
            tileEntityItemStackRenderer = LaserReflectorItemStackRenderer
        }
    }
}