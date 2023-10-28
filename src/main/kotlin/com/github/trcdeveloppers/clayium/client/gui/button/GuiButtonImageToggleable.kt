package com.github.trcdeveloppers.clayium.client.gui.button

import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiButtonImage
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.util.ResourceLocation

class GuiButtonImageToggleable(
    buttonId: Int,
    xIn: Int,
    yIn: Int,
    widthIn: Int,
    heightIn: Int,
    textureOffestX: Int,
    textureOffestY: Int,
    private val xTexStartEnabled: Int,
    private val yTexStartEnabled: Int,
    private val xTexStartEnabledHovered: Int,
    private val yTexStartEnabledHovered: Int,
    private val resourceLocation: ResourceLocation
) : GuiButtonImage(buttonId, xIn, yIn, widthIn, heightIn, textureOffestX, textureOffestY, 0, resourceLocation) {
    constructor(
        buttonId: Int,
        xIn: Int,
        yIn: Int,
        widthIn: Int,
        heightIn: Int,
        textureOffestX: Int,
        textureOffestY: Int,
        resource: ResourceLocation
    ) : this(
        buttonId, xIn, yIn, widthIn, heightIn,
        textureOffestX, textureOffestY,
        textureOffestX, textureOffestY + heightIn,
        textureOffestX, textureOffestY + heightIn * 2,
        resource
    )

    override fun drawButton(mc: Minecraft, mouseX: Int, mouseY: Int, partialTicks: Float) {
        if (enabled) {
            if (visible) {
                hovered = mouseX >= x && mouseY >= y && mouseX < x + width && mouseY < y + height
                mc.textureManager.bindTexture(this.resourceLocation)
                GlStateManager.disableDepth()
                if (hovered) {
                    this.drawTexturedModalRect(x, y, xTexStartEnabledHovered, yTexStartEnabledHovered, width, height)
                } else {
                    this.drawTexturedModalRect(x, y, xTexStartEnabled, yTexStartEnabled, width, height)
                }
                GlStateManager.enableDepth()
            }
        } else {
            super.drawButton(mc, mouseX, mouseY, partialTicks)
        }
    }
}