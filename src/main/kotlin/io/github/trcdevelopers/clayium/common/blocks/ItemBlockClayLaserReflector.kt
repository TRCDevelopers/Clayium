package io.github.trcdevelopers.clayium.common.blocks

import io.github.trcdevelopers.clayium.api.util.CUtils
import io.github.trcdevelopers.clayium.client.renderer.item.LaserReflectorItemStackRenderer
import net.minecraft.item.ItemBlock

class ItemBlockClayLaserReflector(block: BlockClayLaserReflector) : ItemBlock(block) {
    init {
        if (CUtils.isClientSide) {
            tileEntityItemStackRenderer = LaserReflectorItemStackRenderer
        }
    }
}