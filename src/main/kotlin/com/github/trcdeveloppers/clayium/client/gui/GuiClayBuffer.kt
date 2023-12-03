package com.github.trcdeveloppers.clayium.client.gui

import com.github.trcdeveloppers.clayium.Clayium
import com.github.trcdeveloppers.clayium.common.blocks.machine.claybuffer.ContainerClayBuffer
import com.github.trcdeveloppers.clayium.common.blocks.machine.claybuffer.TileClayBuffer
import net.minecraft.client.gui.inventory.GuiContainer
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.resources.I18n
import net.minecraft.inventory.IInventory
import net.minecraft.util.ResourceLocation
import net.minecraftforge.items.SlotItemHandler

class GuiClayBuffer(
    private val tier: Int,
    playerInv: IInventory,
    private val tile: TileClayBuffer,
) : GuiContainer(ContainerClayBuffer(tier, playerInv, tile))  {

    override fun drawGuiContainerForegroundLayer(mouseX: Int, mouseY: Int) {
        fontRenderer.drawString(I18n.format("tile.clayium.buffer_tier$tier"), 6, 6, 0x404040)
    }

    override fun drawGuiContainerBackgroundLayer(partialTicks: Float, mouseX: Int, mouseY: Int) {
        GlStateManager.color(1f, 1f, 1f, 1f)
        val offsetX = (width - xSize) / 2
        val offsetY = (height - ySize) / 2
        this.mc.textureManager.bindTexture(BACKGROUND)
        this.drawTexturedModalRect(offsetX, offsetY, 0, 0, xSize, ySize)
        this.mc.textureManager.bindTexture(PLAYER_INVENTORY)
        this.drawTexturedModalRect(offsetX, offsetY + ContainerClayBuffer.PLAYER_INV_OFFSET_Y - 12, 0, 0, 176, 94)

        this.mc.textureManager.bindTexture(SLOT)
        for (slot in this.inventorySlots.inventorySlots.filterIsInstance<SlotItemHandler>()) {
            this.drawTexturedModalRect(offsetX + slot.xPos - 1, offsetY + slot.yPos - 1, 0, 0, 18, 18)
        }
    }

    override fun drawScreen(mouseX: Int, mouseY: Int, partialTicks: Float) {
        this.drawDefaultBackground()
        super.drawScreen(mouseX, mouseY, partialTicks)
        this.renderHoveredToolTip(mouseX, mouseY)
    }

    companion object {
        private val SLOT = ResourceLocation(Clayium.MOD_ID, "textures/gui/slot.png")
        private val BACKGROUND = ResourceLocation(Clayium.MOD_ID, "textures/gui/back.png")
        private val PLAYER_INVENTORY = ResourceLocation(Clayium.MOD_ID, "textures/gui/gui_playerinventory.png")
    }
}