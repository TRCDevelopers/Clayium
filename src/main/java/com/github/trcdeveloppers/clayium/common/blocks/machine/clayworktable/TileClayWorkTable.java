package com.github.trcdeveloppers.clayium.common.blocks.machine.clayworktable;

import com.github.trcdeveloppers.clayium.common.items.ClayiumItems;
import com.github.trcdeveloppers.clayium.common.recipe.clayworktable.ClayWorkTableRecipe;
import com.github.trcdeveloppers.clayium.common.recipe.clayworktable.ClayWorkTableRecipeManager;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;

import javax.annotation.Nullable;
import java.util.Optional;

public class TileClayWorkTable extends TileEntity {

    private static final Capability<IItemHandler> ITEM_HANDLER_CAPABILITY = CapabilityItemHandler.ITEM_HANDLER_CAPABILITY;
    private final ItemStackHandler handler = new ItemStackHandler(4);
    int craftingProgress = 0;
    int requiredProgress = 0;
    @Nullable
    private ClayWorkTableRecipe currentRecipe = null;
    private final ClayWorkTableRecipeManager recipeManager = ClayWorkTableRecipeManager.INSTANCE;

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        compound.setTag("Inventory", this.handler.serializeNBT());
        return super.writeToNBT(compound);
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        this.handler.deserializeNBT(compound.getCompoundTag("Inventory"));
        super.readFromNBT(compound);
    }

    @Override
    public boolean hasCapability(Capability<?> capability, @Nullable EnumFacing facing) {
        return capability == ITEM_HANDLER_CAPABILITY || super.hasCapability(capability, facing);
    }

    @Nullable
    @Override
    public <T> T getCapability(Capability<T> capability, @Nullable EnumFacing facing) {
        return capability == ITEM_HANDLER_CAPABILITY ? (T) this.handler : super.getCapability(capability, facing);
    }

    private ItemStack getCurrentTool() {
        return this.handler.getStackInSlot(2);
    }

    @SideOnly(Side.CLIENT)
    public int getCraftingProgressScaled(int scale) {
        if (this.requiredProgress == 0) {
            return 0;
        }
        return this.craftingProgress * scale / this.requiredProgress;
    }

    public boolean canPushButton(int id) {
        return this.canStartCraft(this.handler.getStackInSlot(0), ClayWorkTableMethod.fromId(id));
    }

    public void pushButton(int id) {
        ItemStack input = this.handler.getStackInSlot(0);
        ClayWorkTableMethod method = ClayWorkTableMethod.fromId(id);
        if (method == null) {
            throw new NullPointerException("Invalid button id");
        }

        ClayWorkTableRecipe recipe = this.recipeManager.getRecipeFor(input, method).orElseThrow(() -> new NullPointerException("button is pushed without any valid recipe!"));

        if (this.currentRecipe != recipe) {
            this.refreshRecipeWith(recipe);
        }

        this.craftingProgress++;

        if (this.craftingProgress >= this.requiredProgress) {
            input.setCount(input.getCount() - currentRecipe.getInput().getCount());
            if (this.handler.getStackInSlot(2).isEmpty()) {
                this.handler.setStackInSlot(2, currentRecipe.getPrimaryOutput().copy());
            } else {
                this.handler.getStackInSlot(2).setCount(this.handler.getStackInSlot(2).getCount() + currentRecipe.getPrimaryOutput().getCount());
            }
            this.resetRecipe();
        }
    }

    private boolean canStartCraft(ItemStack input, ClayWorkTableMethod method) {
        Optional<ClayWorkTableRecipe> result = this.recipeManager.getRecipeFor(input, method);
        if (!result.isPresent()) {
            return false;
        }
        ClayWorkTableRecipe recipe = result.get();

        if (method == ClayWorkTableMethod.ROLLING_PIN && this.handler.getStackInSlot(1).getItem() != ClayiumItems.getItem("clay_rolling_pin")) {
            return false;
        }
        if ((method == ClayWorkTableMethod.CUT_PLATE || method == ClayWorkTableMethod.CUT)
            && !(this.handler.getStackInSlot(1).getItem() == ClayiumItems.getItem("clay_slicer")
                || this.handler.getStackInSlot(1).getItem() == ClayiumItems.getItem("clay_spatula"))) {
            return false;
        }
        if (method == ClayWorkTableMethod.CUT_DISC && this.handler.getStackInSlot(1).getItem() != ClayiumItems.getItem("clay_spatula")) {
            return false;
        }

        if (recipe.hasSecondaryOutput()) {
            return (this.handler.getStackInSlot(2).isEmpty() || recipe.getPrimaryOutput().isItemEqual(handler.getStackInSlot(2)))
                && recipe.getSecondaryOutput().isItemEqual(this.handler.getStackInSlot(3))
                && recipe.getPrimaryOutput().getCount() + this.handler.getStackInSlot(2).getCount() <= handler.getSlotLimit(2)
                && recipe.getSecondaryOutput().getCount() + this.handler.getStackInSlot(3).getCount() <= handler.getSlotLimit(3);
        } else {
            return (this.handler.getStackInSlot(2).isEmpty() || recipe.getPrimaryOutput().isItemEqual(handler.getStackInSlot(2)))
                && recipe.getPrimaryOutput().getCount() + this.handler.getStackInSlot(2).getCount() <= handler.getSlotLimit(2);
        }
    }

    private void resetRecipe() {
        this.currentRecipe = null;
        this.requiredProgress = 0;
        this.craftingProgress = 0;
    }

    private void refreshRecipeWith(ClayWorkTableRecipe recipe) {
        this.currentRecipe = recipe;
        this.requiredProgress = recipe.getRequiredProcess();
        this.craftingProgress = 0;
    }

    void resetRecipeIfEmptyInput() {
        if (this.handler.getStackInSlot(0).isEmpty()) {
            this.resetRecipe();
        }
    }
}
