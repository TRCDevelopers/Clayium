package com.github.trc.clayium.common.util

import net.minecraft.entity.player.EntityPlayer
import net.minecraft.inventory.Container
import net.minecraft.inventory.IInventory

object DummyContainer : Container() {
    override fun onContainerClosed(playerIn: EntityPlayer) {}
    override fun onCraftMatrixChanged(inventoryIn: IInventory) {}
    override fun detectAndSendChanges() {}
    override fun canInteractWith(playerIn: EntityPlayer) = false
}