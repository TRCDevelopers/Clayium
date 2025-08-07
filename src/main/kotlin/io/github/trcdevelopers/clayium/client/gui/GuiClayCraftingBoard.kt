package io.github.trcdevelopers.clayium.client.gui

import io.github.trcdevelopers.clayium.api.util.clayiumId
import io.github.trcdevelopers.clayium.common.blocks.claycraftingtable.TileClayCraftingBoard
import io.github.trcdevelopers.clayium.common.gui.ContainerClayCraftingBoard
import net.minecraft.client.gui.inventory.GuiContainer
import net.minecraft.inventory.IInventory

class GuiClayCraftingBoard(
    playerInv: IInventory,
    val tile: TileClayCraftingBoard,
) : GuiContainer(ContainerClayCraftingBoard(playerInv, tile)) {

    private val PLAYER_INVENTORY = clayiumId("textures/gui/player_inventory.png")
    private val BACK = clayiumId("textures/gui/gui_back.png")
    private val TOP = clayiumId("textures/gui/gui_top.png")
    private val BOTTOM = clayiumId("textures/gui/gui_bottom.png")
    private val LEFT = clayiumId("textures/gui/gui_left.png")
    private val RIGHT = clayiumId("textures/gui/gui_right.png")
    private val TOP_LEFT = clayiumId("textures/gui/gui_top_left.png")
    private val TOP_RIGHT = clayiumId("textures/gui/gui_top_right.png")
    private val BOTTOM_LEFT = clayiumId("textures/gui/gui_bottom_left.png")
    private val BOTTOM_RIGHT = clayiumId("textures/gui/gui_bottom_right.png")
    private val SLOT = clayiumId("textures/gui/slot.png")


    override fun drawGuiContainerBackgroundLayer(partialTicks: Float, mouseX: Int, mouseY: Int) {
        val x = (width - xSize) / 2
        val y = (height - ySize) / 2

        this.mc.textureManager.bindTexture(BACK)
        this.drawTexturedModalRect(x + 4, y + 4, 0, 0, xSize - 8, ySize - 8)

        this.mc.textureManager.bindTexture(TOP)
        drawScaledCustomSizeModalRect(x + 4, y, 0f, 0f, 1, 4, xSize - 8, 4, 1f, 4f)
        this.mc.textureManager.bindTexture(LEFT)
        drawScaledCustomSizeModalRect(x, y + 4, 0f, 0f, 4, 1, 4, ySize - 8, 4f, 1f)
        this.mc.textureManager.bindTexture(BOTTOM)
        drawScaledCustomSizeModalRect(x + 4, y + ySize - 4, 0f, 0f, 1, 4, xSize - 8, 4, 1f, 4f)
        this.mc.textureManager.bindTexture(RIGHT)
        drawScaledCustomSizeModalRect(x + xSize - 4, y + 4, 0f, 0f, 4, 1, 4, ySize - 8, 4f, 1f)

        this.mc.textureManager.bindTexture(TOP_LEFT)
        drawScaledCustomSizeModalRect(x, y, 0f, 0f, 4, 4, 4, 4, 4f, 4f)
        this.mc.textureManager.bindTexture(TOP_RIGHT)
        drawScaledCustomSizeModalRect(x + xSize - 4, y, 0f, 0f, 4, 4, 4, 4, 4f, 4f)
        this.mc.textureManager.bindTexture(BOTTOM_LEFT)
        drawScaledCustomSizeModalRect(x, y + ySize - 4, 0f, 0f, 4, 4, 4, 4, 4f, 4f)
        this.mc.textureManager.bindTexture(BOTTOM_RIGHT)
        drawScaledCustomSizeModalRect(x + xSize - 4, y + ySize - 4, 0f, 0f, 4, 4, 4, 4, 4f, 4f)
    }

    override fun drawScreen(mouseX: Int, mouseY: Int, partialTicks: Float) {
        drawDefaultBackground()
        super.drawScreen(mouseX, mouseY, partialTicks)
        renderHoveredToolTip(mouseX, mouseY)
    }
}