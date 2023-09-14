package com.github.trcdeveloppers.clayium.gui;

import com.github.trcdeveloppers.clayium.blocks.machines.container.ClayWorktableContainer;
import com.github.trcdeveloppers.clayium.blocks.machines.tile.TileClayWorkTable;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.inventory.IInventory;
import net.minecraft.util.ResourceLocation;

import static com.github.trcdeveloppers.clayium.Clayium.MOD_ID;

public class GuiClayWorkTable extends GuiContainer {
    public GuiClayWorkTable(IInventory playerInv, TileClayWorkTable te) {
        super(new ClayWorktableContainer(playerInv, te));
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        super.drawGuiContainerForegroundLayer(mouseX, mouseY);
        this.fontRenderer.drawString(I18n.format("recipe.ClayWorkTable"), 6, 6, 0x404040);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        GlStateManager.color(1f, 1f, 1f, 1f);
        this.mc.getTextureManager().bindTexture(new ResourceLocation(MOD_ID, "textures/gui/clayworktable.png"));
        this.drawTexturedModalRect(this.guiLeft, this.guiTop, 0, 0, this.xSize, this.ySize);
    }
}
