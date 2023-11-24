package com.github.trcdeveloppers.clayium.common.blocks.machine.claybuffer

import com.github.trcdeveloppers.clayium.common.blocks.machine.ContainerClayium
import net.minecraft.inventory.IInventory

class ClayBufferContainer(
    tier: Int,
    playerInv: IInventory,
    private val tile: TileClayBuffer
) : ContainerClayium(playerInv) {

    init {

    }

    companion object {

    }
}