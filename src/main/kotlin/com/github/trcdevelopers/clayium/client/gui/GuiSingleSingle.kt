package com.github.trcdevelopers.clayium.client.gui

import com.github.trcdevelopers.clayium.common.Clayium
import com.github.trcdevelopers.clayium.common.blocks.machine.single_single.ContainerSingleSingle
import com.github.trcdevelopers.clayium.common.blocks.machine.single_single.TileSingleSingle
import net.minecraft.client.gui.inventory.GuiContainer
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.inventory.IInventory
import net.minecraft.util.ResourceLocation

class GuiSingleSingle(
    playerInv: IInventory,
    private val tile: TileSingleSingle
) : GuiContainer(ContainerSingleSingle(playerInv, tile)) {

    override fun drawGuiContainerBackgroundLayer(partialTicks: Float, mouseX: Int, mouseY: Int) {
        GlStateManager.pushMatrix()
        GlStateManager.color(1f, 1f, 1f, 1f)
        this.mc.textureManager.bindTexture(BACKGROUND)
        this.drawTexturedModalRect(guiLeft, guiTop, 0, 0, xSize, ySize - 94)
        this.mc.textureManager.bindTexture(PLAYER_INVENTORY)
        this.drawTexturedModalRect(guiLeft, guiTop + 72, 0, 0, 176, 94)
        this.drawLargeSlot(39, 30)
        this.drawLargeSlot(111, 30)
        this.drawProgressBar()
        GlStateManager.popMatrix()
    }

    override fun drawScreen(mouseX: Int, mouseY: Int, partialTicks: Float) {
        this.drawDefaultBackground()
        super.drawScreen(mouseX, mouseY, partialTicks)
        this.renderHoveredToolTip(mouseX, mouseY)
    }

    private fun drawLargeSlot(x: Int, y: Int) {
        this.mc.textureManager.bindTexture(SLOT)
        this.drawTexturedModalRect(guiLeft + x, guiTop + y, 0, 32, 26, 26)
    }

    private fun drawProgressBar() {
        this.mc.textureManager.bindTexture(PROGRESS_BAR)
        this.drawTexturedModalRect(guiLeft + 39 + 26 + 12, guiTop + 29 + 6, 1, 1, 22, 15)
    }

    companion object {
        private val SLOT = ResourceLocation(Clayium.MOD_ID, "textures/gui/slot.png")
        private val BACKGROUND = ResourceLocation(Clayium.MOD_ID, "textures/gui/back.png")
        private val PLAYER_INVENTORY = ResourceLocation(Clayium.MOD_ID, "textures/gui/playerinventory.png")
        private val PROGRESS_BAR = ResourceLocation(Clayium.MOD_ID, "textures/gui/progress_bar.png")
    }
}