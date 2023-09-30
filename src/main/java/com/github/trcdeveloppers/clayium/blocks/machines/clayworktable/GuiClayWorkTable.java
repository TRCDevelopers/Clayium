package com.github.trcdeveloppers.clayium.blocks.machines.clayworktable;

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
        this.fontRenderer.drawString(I18n.format("recipe.ClayWorkTable"), 6, 6, 0x404040);
        this.fontRenderer.drawString(I18n.format("container.inventory"), 8, this.ySize - 94, 0x404040);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        GlStateManager.color(1f, 1f, 1f, 1f);
        this.mc.getTextureManager().bindTexture(GUI_IMAGE);
        this.drawTexturedModalRect(this.guiLeft, this.guiTop, 0, 0, this.xSize, this.ySize);
        // progress bar
        this.drawTexturedModalRect(this.guiLeft + 48, this.guiTop + 29, 176, 0, this.tile.getCraftingProgressScaled(80), 16);
        for (GuiButton button : this.buttonList) {
            button.enabled = this.tile.canPushButton(button.id);
        }
    }

    @Override
    public void initGui() {
        super.initGui();
        this.buttonList.add(new GuiButtonImageToggleable(ClayWorkTableMethod.ROLLING_HAND.id, this.guiLeft + 40, this.guiTop + 52, 16, 16, 176, 32, GUI_IMAGE));
        this.buttonList.add(new GuiButtonImageToggleable(ClayWorkTableMethod.PUNCH.id, this.guiLeft + 56, this.guiTop + 52, 16, 16, 192, 32, GUI_IMAGE));
        this.buttonList.add(new GuiButtonImageToggleable(ClayWorkTableMethod.ROLLING_PIN.id, this.guiLeft + 72, this.guiTop + 52, 16, 16, 208, 32, GUI_IMAGE));
        this.buttonList.add(new GuiButtonImageToggleable(ClayWorkTableMethod.CUT_PLATE.id, this.guiLeft + 88, this.guiTop + 52, 16, 16, 224, 32, GUI_IMAGE));
        this.buttonList.add(new GuiButtonImageToggleable(ClayWorkTableMethod.CUT_DISC.id, this.guiLeft + 104, this.guiTop + 52, 16, 16, 240, 32, GUI_IMAGE));
        this.buttonList.add(new GuiButtonImageToggleable(ClayWorkTableMethod.CUT.id, this.guiLeft + 120, this.guiTop + 52, 16, 16, 176, 80, GUI_IMAGE));
    }

    @Override
    protected void actionPerformed(GuiButton button) {
        if (!button.enabled) {
            return;
        }
        // calls Container.enchantItem
        this.mc.playerController.sendEnchantPacket(this.inventorySlots.windowId, button.id);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawDefaultBackground();
        super.drawScreen(mouseX, mouseY, partialTicks);
        this.renderHoveredToolTip(mouseX, mouseY);
    }
}
