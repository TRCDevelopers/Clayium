package com.github.trc.clayium.client.gui

import com.github.trc.clayium.api.util.clayiumId
import com.github.trc.clayium.client.gui.button.GuiButtonImageToggleable
import com.github.trc.clayium.common.blocks.clayworktable.ClayWorkTableMethod
import com.github.trc.clayium.common.blocks.clayworktable.TileClayWorkTable
import com.github.trc.clayium.common.gui.ContainerClayWorkTable
import net.minecraft.client.gui.GuiButton
import net.minecraft.client.gui.inventory.GuiContainer
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.resources.I18n
import net.minecraft.inventory.IInventory
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly

@SideOnly(Side.CLIENT)
class GuiClayWorkTable(
    playerInv: IInventory,
    private val tile: TileClayWorkTable
) : GuiContainer(ContainerClayWorkTable(playerInv, tile)) {
    override fun drawGuiContainerForegroundLayer(mouseX: Int, mouseY: Int) {
        fontRenderer.drawString(I18n.format("recipe.clayium.ClayWorkTable"), 6, 6, 0x404040)
        fontRenderer.drawString(I18n.format("container.inventory"), 8, ySize - 94, 0x404040)
    }

    override fun drawGuiContainerBackgroundLayer(partialTicks: Float, mouseX: Int, mouseY: Int) {
        GlStateManager.color(1f, 1f, 1f, 1f)
        mc.textureManager.bindTexture(GUI_IMAGE)
        this.drawTexturedModalRect(guiLeft, guiTop, 0, 0, xSize, ySize)
        // progress bar
        this.drawTexturedModalRect(guiLeft + 48, guiTop + 29, 176, 0, tile.getCraftingProgressScaled(80), 16)
        for (button in buttonList) {
            button.enabled = tile.canPushButton(button.id)
        }
    }

    override fun initGui() {
        super.initGui()
        buttonList.addAll(listOf(
            GuiButtonImageToggleable(
                ClayWorkTableMethod.ROLLING_HAND.id,
                guiLeft + 40, guiTop + 52,
                16, 16,
                176, 32,
                GUI_IMAGE,
            ),
            GuiButtonImageToggleable(
                ClayWorkTableMethod.PUNCH.id,
                guiLeft + 56, guiTop + 52,
                16, 16,
                192, 32,
                GUI_IMAGE,
            ),
            GuiButtonImageToggleable(
                ClayWorkTableMethod.ROLLING_PIN.id,
                guiLeft + 72, guiTop + 52,
                16, 16,
                208, 32,
                GUI_IMAGE,
            ),
            GuiButtonImageToggleable(
                ClayWorkTableMethod.CUT_PLATE.id,
                guiLeft + 88, guiTop + 52,
                16, 16,
                224, 32,
                GUI_IMAGE,
            ),
            GuiButtonImageToggleable(
                ClayWorkTableMethod.CUT_DISC.id,
                guiLeft + 104, guiTop + 52,
                16, 16,
                240, 32,
                GUI_IMAGE,
            ),
            GuiButtonImageToggleable(
                ClayWorkTableMethod.CUT.id,
                guiLeft + 120, guiTop + 52,
                16, 16,
                176, 80,
                GUI_IMAGE,
            )
        ))
    }

    override fun actionPerformed(button: GuiButton) {
        if (!button.enabled) {
            return
        }
        // calls Container.enchantItem
        mc.playerController.sendEnchantPacket(inventorySlots.windowId, button.id)
    }

    override fun drawScreen(mouseX: Int, mouseY: Int, partialTicks: Float) {
        drawDefaultBackground()
        super.drawScreen(mouseX, mouseY, partialTicks)
        renderHoveredToolTip(mouseX, mouseY)
    }

    companion object {
        private val GUI_IMAGE = clayiumId("textures/gui/clayworktable.png")
    }
}
