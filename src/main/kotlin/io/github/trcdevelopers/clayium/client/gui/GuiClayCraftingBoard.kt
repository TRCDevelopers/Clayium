package io.github.trcdevelopers.clayium.client.gui

import io.github.trcdevelopers.clayium.common.blocks.claycraftingtable.TileClayCraftingBoard
import io.github.trcdevelopers.clayium.common.gui.ContainerClayCraftingBoard
import net.minecraft.client.gui.inventory.GuiContainer
import net.minecraft.inventory.IInventory

class GuiClayCraftingBoard(
    playerInv: IInventory,
    val tile: TileClayCraftingBoard,
) : GuiContainer(ContainerClayCraftingBoard(playerInv, tile)) {
    override fun drawGuiContainerBackgroundLayer(partialTicks: Float, mouseX: Int, mouseY: Int) {
    }

    override fun drawScreen(mouseX: Int, mouseY: Int, partialTicks: Float) {
        drawDefaultBackground()
        super.drawScreen(mouseX, mouseY, partialTicks)
        renderHoveredToolTip(mouseX, mouseY)
    }
}