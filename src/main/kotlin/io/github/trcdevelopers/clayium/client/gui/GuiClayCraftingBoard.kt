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
) : GuiContainer(ContainerClayCraftingBoard(playerInv, tile)) {

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

    override fun drawGuiContainerForegroundLayer(mouseX: Int, mouseY: Int) {
        fontRenderer.drawString(I18n.format("tile.clayium.clay_crafting_board.name"), 6, 6, 0x404040)
        fontRenderer.drawString(I18n.format("container.inventory"), 8, ySize - 94, 0x404040)
    }

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

        this.mc.textureManager.bindTexture(PLAYER_INVENTORY)
        this.drawTexturedModalRect(x + xSize - 176, y + ySize - 94, 0, 0, 176, 94)

        this.mc.textureManager.bindTexture(SLOT)
        for (i in 0..<3) {
            for (j in 0..<3) {
                val slotX = (j * 18) + (x + 29)
                val slotY = (i * 18) + (y + 16)
                this.drawTexturedModalRect(slotX, slotY, 0, 0, 18, 18)
            }
        }
        this.drawTexturedModalRect(x + 119, y + 30, 0, 32, 26, 26)
    }

    override fun drawScreen(mouseX: Int, mouseY: Int, partialTicks: Float) {
        drawDefaultBackground()
        super.drawScreen(mouseX, mouseY, partialTicks)
        renderHoveredToolTip(mouseX, mouseY)
    }
}