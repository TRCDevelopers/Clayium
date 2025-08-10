package io.github.trcdevelopers.clayium.client.gui

import io.github.trcdevelopers.clayium.api.util.clayiumId
import io.github.trcdevelopers.clayium.common.blocks.claycraftingtable.TileClayCraftingBoard
import io.github.trcdevelopers.clayium.common.gui.ContainerClayCraftingBoard
import net.minecraft.client.gui.inventory.GuiContainer
import net.minecraft.client.resources.I18n
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.world.World

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
private val PROGRESS_BAR = clayiumId("textures/gui/progress_bar.png")

class GuiClayCraftingBoard(
    player: EntityPlayer,
    world: World,
    val tile: TileClayCraftingBoard,
    val container: ContainerClayCraftingBoard = ContainerClayCraftingBoard(player, world, tile),
) : GuiContainer(container) {

    init {
        if (container.hasNeighbor) {
            this.ySize = 129 + 13 + (18*3 + 4) + 25 // (137 + 4 is crafting + neighbor inv + 13 padding), (18*3 + 4 is player inv height), (30 is hotbar + padding)
        }
    }

    override fun drawGuiContainerForegroundLayer(mouseX: Int, mouseY: Int) {
        fontRenderer.drawString(I18n.format("tile.clayium.clay_crafting_board.name"), 6, 6, 0x404040)
        fontRenderer.drawString(I18n.format("container.inventory"), 8, ySize - 94, 0x404040)
    }

    override fun drawGuiContainerBackgroundLayer(partialTicks: Float, mouseX: Int, mouseY: Int) {
        val x = (width - xSize) / 2
        val y = (height - ySize) / 2

        // background
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

        // Inventory Slots
        this.mc.textureManager.bindTexture(SLOT)
        for (slot in container.inventorySlots) {
            this.drawTexturedModalRect(x + slot.xPos - 1, y + slot.yPos - 1, 0, 0, 18, 18)
        }

        // Crafting Result
        this.drawTexturedModalRect(x + 119, y + 30, 0, 32, 26, 26)

        // progress bar at (90, 35)
        this.mc.textureManager.bindTexture(PROGRESS_BAR)
        this.drawTexturedModalRect(x + 90, y + 35, 1, 1, 22, 15)

    }

    override fun drawScreen(mouseX: Int, mouseY: Int, partialTicks: Float) {
        drawDefaultBackground()
        super.drawScreen(mouseX, mouseY, partialTicks)
        renderHoveredToolTip(mouseX, mouseY)
    }
}