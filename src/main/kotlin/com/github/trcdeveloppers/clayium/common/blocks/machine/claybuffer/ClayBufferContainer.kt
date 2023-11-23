package com.github.trcdeveloppers.clayium.common.blocks.machine.claybuffer

import net.minecraft.entity.player.EntityPlayer
import net.minecraft.inventory.Container
import net.minecraft.inventory.IInventory

class ClayBufferContainer(
    tier: Int,
    playerInv: IInventory,
    private val tile: TileClayBuffer
) : Container() {

    init {

    }

    override fun canInteractWith(playerIn: EntityPlayer): Boolean {
        return !playerIn.isSpectator
    }
}