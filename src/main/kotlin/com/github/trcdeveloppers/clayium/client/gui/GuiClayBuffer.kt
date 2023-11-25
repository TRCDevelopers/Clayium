package com.github.trcdeveloppers.clayium.client.gui

import com.github.trcdeveloppers.clayium.common.blocks.machine.claybuffer.ClayBufferContainer
import com.github.trcdeveloppers.clayium.common.blocks.machine.claybuffer.TileClayBuffer
import net.minecraft.client.gui.inventory.GuiContainer
import net.minecraft.inventory.IInventory

class GuiClayBuffer(
    tier: Int,
    playerInv: IInventory,
    private val tile: TileClayBuffer,
) : GuiContainer(ClayBufferContainer(tier, playerInv, tile))  {
    override fun drawGuiContainerBackgroundLayer(partialTicks: Float, mouseX: Int, mouseY: Int) {
        TODO("Not yet implemented")
    }
}