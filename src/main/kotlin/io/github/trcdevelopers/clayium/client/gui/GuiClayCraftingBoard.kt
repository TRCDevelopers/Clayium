package io.github.trcdevelopers.clayium.client.gui

import io.github.trcdevelopers.clayium.api.util.clayiumId
import io.github.trcdevelopers.clayium.common.blocks.claycraftingtable.TileClayCraftingBoard
import io.github.trcdevelopers.clayium.common.gui.ContainerClayCraftingBoard
import net.minecraft.client.gui.inventory.GuiContainer
import net.minecraft.client.resources.I18n
import net.minecraft.inventory.IInventory
import net.minecraft.util.ResourceLocation

class GuiClayCraftingBoard(
    playerInv: IInventory,
    val tile: TileClayCraftingBoard,
    val container: ContainerClayCraftingBoard = ContainerClayCraftingBoard(playerInv, tile),
) : GuiContainer(container) {

    private val CRAFTING_TABLE = ResourceLocation("textures/gui/container/crafting_table.png")
    private val PLAYER_INVENTORY = clayiumId("textures/gui/gui_player_inventory.png")
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

    init {
        if (container.hasNeighbor) {
            this.ySize = 137 + 13 + (18*3 + 4) + 25 // (137 + 4 is crafting + neighbor inv + 13 padding), (18*3 + 4 is player inv height), (30 is hotbar + padding)
        }
    }

    override fun drawGuiContainerForegroundLayer(mouseX: Int, mouseY: Int) {
        fontRenderer.drawString(I18n.format("tile.clayium.clay_crafting_board.name"), 6, 6, 0x404040)
        fontRenderer.drawString(I18n.format("container.inventory"), 8, ySize - 94, 0x404040)
    }

    override fun drawGuiContainerBackgroundLayer(partialTicks: Float, mouseX: Int, mouseY: Int) {
        if (container.hasNeighbor) {
            drawWithNeighbouringInventory()
        } else {
            drawNormal()
        }
    }

    private fun drawNormal() {
        val x = (width - xSize) / 2
        val y = (height - ySize) / 2
        mc.textureManager.bindTexture(CRAFTING_TABLE)
        drawTexturedModalRect(x, y, 0, 0, xSize, ySize)
    }

    private fun drawWithNeighbouringInventory() {
        val x = (width - xSize) / 2
        val y = (height - ySize) / 2

        mc.textureManager.bindTexture(CRAFTING_TABLE)
        drawTexturedModalRect(x, y, 0, 0, xSize, 137)
        drawTexturedModalRect(x, y + 137, 0, 70, xSize, 13 + 18*3 + 4 + 25)
    }

    override fun drawScreen(mouseX: Int, mouseY: Int, partialTicks: Float) {
        drawDefaultBackground()
        super.drawScreen(mouseX, mouseY, partialTicks)
        renderHoveredToolTip(mouseX, mouseY)
    }
}