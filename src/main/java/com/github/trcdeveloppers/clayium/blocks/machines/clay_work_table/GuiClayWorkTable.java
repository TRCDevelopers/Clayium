package com.github.trcdeveloppers.clayium.blocks.machines.clay_work_table;

import com.github.trcdeveloppers.clayium.gui.GuiButtonImageToggleable;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.inventory.IInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import static com.github.trcdeveloppers.clayium.Clayium.MOD_ID;

@SideOnly(Side.CLIENT)
public class GuiClayWorkTable extends GuiContainer {
    private static final ResourceLocation GUI_IMAGE = new ResourceLocation(MOD_ID, "textures/gui/clayworktable.png");
    private final TileClayWorkTable tile;
    public GuiClayWorkTable(IInventory playerInv, TileClayWorkTable te) {
        super(new ClayWorktableContainer(playerInv, te));
        this.tile = te;
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        super.drawGuiContainerForegroundLayer(mouseX, mouseY);
        this.fontRenderer.drawString(I18n.format("recipe.ClayWorkTable"), 6, 6, 0x404040);
        this.fontRenderer.drawString(I18n.format("container.inventory"), 8, this.ySize - 94, 0x404040);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        GlStateManager.color(1f, 1f, 1f, 1f);
        this.mc.getTextureManager().bindTexture(GUI_IMAGE);
        this.drawTexturedModalRect(this.guiLeft, this.guiTop, 0, 0, this.xSize, this.ySize);
        // isBurningに相当するものを追加するかも？
        if (this.tile.requiredProgress != 0) {
            int i1 = 6;
//            this.drawTexturedModalRect(this.guiLeft + 56, this.guiTop + 36 + 12 - i1, 176, 12 - i1, 14, i1 + 2);
        }
        // progress bar
        this.drawTexturedModalRect(this.guiLeft + 48, this.guiTop + 29, 176, 0, this.tile.getCraftingProgressScaled(80), 16);
        for (GuiButton button : this.buttonList) {
            button.enabled = this.tile.canPushButton(button.id);
        }
    }

    @Override
    public void initGui() {
        super.initGui();
        this.buttonList.add(new GuiButtonImageToggleable(1, this.guiLeft + 40, this.guiTop + 52, 16, 16, 176, 32, GUI_IMAGE));
        this.buttonList.add(new GuiButtonImageToggleable(2, this.guiLeft + 56, this.guiTop + 52, 16, 16, 192, 32, GUI_IMAGE));
        this.buttonList.add(new GuiButtonImageToggleable(3, this.guiLeft + 72, this.guiTop + 52, 16, 16, 208, 32, GUI_IMAGE));
        this.buttonList.add(new GuiButtonImageToggleable(4, this.guiLeft + 88, this.guiTop + 52, 16, 16, 224, 32, GUI_IMAGE));
        this.buttonList.add(new GuiButtonImageToggleable(5, this.guiLeft + 104, this.guiTop + 52, 16, 16, 240, 32, GUI_IMAGE));
        this.buttonList.add(new GuiButtonImageToggleable(6, this.guiLeft + 120, this.guiTop + 52, 16, 16, 176, 80, GUI_IMAGE));
    }

    @Override
    protected void actionPerformed(GuiButton button) {
        if (!button.enabled) {
            return;
        }
    }
}
