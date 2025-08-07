package io.github.trcdevelopers.clayium.common.gui

import io.github.trcdevelopers.clayium.common.blocks.claycraftingtable.TileClayCraftingBoard
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.inventory.Container
import net.minecraft.inventory.IInventory

class ContainerClayCraftingBoard(
    playerInv: IInventory,
    val tile: TileClayCraftingBoard,
) : Container() {

    init {

    }

    override fun canInteractWith(playerIn: EntityPlayer): Boolean {
        return playerIn.isSpectator
    }
}