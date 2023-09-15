package com.github.trcdeveloppers.clayium.blocks.machines.clay_work_table;

import com.github.trcdeveloppers.clayium.blocks.machines.clay_work_table.ClayWorktableContainer;
import com.github.trcdeveloppers.clayium.blocks.machines.clay_work_table.TileClayWorkTable;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiButtonImage;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.inventory.IInventory;
import net.minecraft.util.ResourceLocation;

import java.io.IOException;

import static com.github.trcdeveloppers.clayium.Clayium.MOD_ID;

public class GuiClayWorkTable extends GuiContainer {
    private static final ResourceLocation GUI_IMAGE = new ResourceLocation(MOD_ID, "textures/gui/clayworktable.png");
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
        this.mc.getTextureManager().bindTexture(GUI_IMAGE);
        this.drawTexturedModalRect(this.guiLeft, this.guiTop, 0, 0, this.xSize, this.ySize);
//        for (int i = 0; i < 6; i ++) {
//            this.buttonList.get(i);
//        }
    }

    @Override
    public void initGui() {
        super.initGui();
        int x = (this.width - this.xSize) / 2;
        int y = (this.height - this.ySize) / 2;
        this.buttonList.add(new GuiButtonImage(1,x + 40, y + 52, 16, 16, 176, 32, 0, GUI_IMAGE));
        this.buttonList.add(new GuiButtonImage(2,x + 56, y + 52, 16, 16, 192, 32, 0, GUI_IMAGE));
        this.buttonList.add(new GuiButtonImage(3,x + 72, y + 52, 16, 16, 208, 32, 0, GUI_IMAGE));
        this.buttonList.add(new GuiButtonImage(4,x + 88, y + 52, 16, 16, 224, 32, 0, GUI_IMAGE));
        this.buttonList.add(new GuiButtonImage(5,x + 104, y + 52, 16, 16, 240, 32, 0, GUI_IMAGE));
        this.buttonList.add(new GuiButtonImage(6,x + 120, y + 52, 16, 16, 176, 80, 0, GUI_IMAGE));
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        if (!button.enabled) {
            return;
        }
    }

}
