package com.github.trcdevelopers.clayium.client.gui

import com.github.trcdevelopers.clayium.common.Clayium
import com.github.trcdevelopers.clayium.common.blocks.machine.tile.TileSingle2SingleMachine
import com.github.trcdevelopers.clayium.common.gui.ContainerSingle2SingleMachine
import net.minecraft.client.gui.inventory.GuiContainer
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.resources.I18n
import net.minecraft.inventory.IInventory
import net.minecraft.util.ResourceLocation

class GuiSingle2SingleMachine(
    playerInv: IInventory,
    private val tile: TileSingle2SingleMachine,
) : GuiContainer(
    ContainerSingle2SingleMachine(playerInv, tile)
) {

    override fun drawGuiContainerForegroundLayer(mouseX: Int, mouseY: Int) {
        //todo
        // fontRenderer.drawString(I18n.format(""), 6, 6, 0x404040)
        fontRenderer.drawString(I18n.format("container.inventory"), 8, ySize - 94, 0x404040)
        fontRenderer.drawString(I18n.format("tooltip.clayium.ce", tile.storedCe.toString()), 4, 60, 0x404040)
    }

    override fun drawGuiContainerBackgroundLayer(partialTicks: Float, mouseX: Int, mouseY: Int) {
        GlStateManager.pushMatrix()
        GlStateManager.color(1f, 1f, 1f, 1f)
        this.mc.textureManager.bindTexture(BACKGROUND)
        this.drawTexturedModalRect(guiLeft, guiTop, 0, 0, xSize, ySize - 94)
        this.mc.textureManager.bindTexture(PLAYER_INVENTORY)
        this.drawTexturedModalRect(guiLeft, guiTop + 72, 0, 0, 176, 94)

        this.mc.textureManager.bindTexture(SLOT)
        this.drawTexturedModalRect(guiLeft + 39, guiTop + 30, 0, 32, 26, 26)
        this.drawTexturedModalRect(guiLeft + 111, guiTop + 30, 0, 32, 26, 26)

        GlStateManager.popMatrix()
    }

    override fun drawScreen(mouseX: Int, mouseY: Int, partialTicks: Float) {
        this.drawDefaultBackground()
        super.drawScreen(mouseX, mouseY, partialTicks)
        this.renderHoveredToolTip(mouseX, mouseY)
    }

    companion object {
        private val SLOT = ResourceLocation(Clayium.MOD_ID, "textures/gui/slot.png")
        private val BACKGROUND = ResourceLocation(Clayium.MOD_ID, "textures/gui/back.png")
        private val PLAYER_INVENTORY = ResourceLocation(Clayium.MOD_ID, "textures/gui/playerinventory.png")
    }
}