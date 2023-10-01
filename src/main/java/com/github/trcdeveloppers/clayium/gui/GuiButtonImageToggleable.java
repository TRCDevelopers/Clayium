package com.github.trcdeveloppers.clayium.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButtonImage;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;

public class GuiButtonImageToggleable extends GuiButtonImage {

    protected final ResourceLocation resourceLocation;

    protected final int xTexStartEnabled;
    protected final int yTexStartEnabled;

    protected final int xTexStartEnabledHovered;
    protected final int yTexStartEnabledHovered;

    public GuiButtonImageToggleable(int buttonId, int xIn, int yIn, int widthIn, int heightIn, int textureOffestX, int textureOffestY, int xTexStartEnabled, int yTexStartEnabled, int xTexStartEnabledHovered, int yTexStartEnabledHovered, ResourceLocation resource) {
        super(buttonId, xIn, yIn, widthIn, heightIn, textureOffestX, textureOffestY, 0, resource);
        this.xTexStartEnabled = xTexStartEnabled;
        this.yTexStartEnabled = yTexStartEnabled;
        this.xTexStartEnabledHovered = xTexStartEnabledHovered;
        this.yTexStartEnabledHovered = yTexStartEnabledHovered;
        this.resourceLocation = resource;
    }

    public GuiButtonImageToggleable(int buttonId, int xIn, int yIn, int widthIn, int heightIn, int textureOffestX, int textureOffestY, ResourceLocation resource)
    {
        this(
                buttonId, xIn, yIn, widthIn, heightIn,
                textureOffestX, textureOffestY,
                textureOffestX, textureOffestY + heightIn,
                textureOffestX, textureOffestY + heightIn * 2,
                resource
        );
    }

    @Override
    public void drawButton(Minecraft mc, int mouseX, int mouseY, float partialTicks) {
        if (this.enabled) {
            if (this.visible) {
                this.hovered = mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height;
                mc.getTextureManager().bindTexture(this.resourceLocation);
                GlStateManager.disableDepth();

                if (this.hovered) {
                    this.drawTexturedModalRect(this.x, this.y, this.xTexStartEnabledHovered, this.yTexStartEnabledHovered, this.width, this.height);
                } else {
                    this.drawTexturedModalRect(this.x, this.y, this.xTexStartEnabled, this.yTexStartEnabled, this.width, this.height);
                }

                GlStateManager.enableDepth();
            }
        } else {
            super.drawButton(mc, mouseX, mouseY, partialTicks);
        }
    }
}