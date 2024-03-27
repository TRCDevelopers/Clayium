package com.github.trcdevelopers.clayium.client.gui

import com.github.trcdevelopers.clayium.common.Clayium
import com.github.trcdevelopers.clayium.common.gui.ContainerClayBuffer
import com.github.trcdevelopers.clayium.common.blocks.machine.tile.TileClayBuffer
import net.minecraft.client.gui.inventory.GuiContainer
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.resources.I18n
import net.minecraft.inventory.IInventory
import net.minecraft.util.ResourceLocation
import net.minecraftforge.items.SlotItemHandler

class GuiClayBuffer(
    playerInv: IInventory,
    private val tile: TileClayBuffer,
) : GuiContainer(ContainerClayBuffer(playerInv, tile))  {

    init {
        this.ySize = 18 + tile.inventoryY * 18 + 94
    }

    override fun drawGuiContainerForegroundLayer(mouseX: Int, mouseY: Int) {
        fontRenderer.drawString(I18n.format("tile.clayium.clay_buffer_tier${tile.tier}.name"), 6, 6, 0x404040)
        fontRenderer.drawString(I18n.format("container.inventory"), 6, 18 + tile.inventoryY * 18, 0x404040)
    }

    override fun drawGuiContainerBackgroundLayer(partialTicks: Float, mouseX: Int, mouseY: Int) {
        GlStateManager.color(1f, 1f, 1f, 1f)
        this.mc.textureManager.bindTexture(BACKGROUND)
        this.drawTexturedModalRect(guiLeft, guiTop, 0, 0, xSize, ySize - 94)
        this.mc.textureManager.bindTexture(PLAYER_INVENTORY)
        this.drawTexturedModalRect(guiLeft, guiTop + 18 + this.tile.inventoryY * 18, 0, 0, 176, 94)

        this.mc.textureManager.bindTexture(SLOT)
        for (slot in this.inventorySlots.inventorySlots.filterIsInstance<SlotItemHandler>()) {
            this.drawTexturedModalRect(guiLeft + slot.xPos - 1, guiTop + slot.yPos - 1, 0, 0, 18, 18)
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
        private val PLAYER_INVENTORY = ResourceLocation(Clayium.MOD_ID, "textures/gui/playerinventory.png")
    }
}