package com.github.trcdeveloppers.clayium.blocks.machines.clayworktable;

import com.github.trcdeveloppers.clayium.items.ClayiumItems;
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

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class TileClayWorkTable extends TileEntity {

    static final Capability<IItemHandler> ITEM_HANDLER_CAPABILITY = CapabilityItemHandler.ITEM_HANDLER_CAPABILITY;
    private final ItemStackHandler handler = new ItemStackHandler(4);
    int craftingProgress = 0;
    int requiredProgress = 0;
    @Nonnull
    private ClayWorkTableRecipes.Recipe currentRecipe = ClayWorkTableRecipes.Recipe.EMPTY;

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

    public boolean isCrafting() {
        return !this.currentRecipe.isEmpty();
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

    void pushButton(int id) {
        if (this.currentRecipe.METHOD.id != id) {
            this.resetRecipe();
        }
        ItemStack input = this.handler.getStackInSlot(0);
        if (!this.isCrafting()) {
            this.currentRecipe = ClayWorkTableRecipes.getRecipeFor(input, ClayWorkTableMethod.fromId(id));
            this.requiredProgress = this.currentRecipe.CLICKS;
            this.craftingProgress = 1;
            return;
        }
        this.craftingProgress++;
        if (this.craftingProgress >= this.requiredProgress) {
            input.setCount(input.getCount() - currentRecipe.INPUT.getCount());
            if (this.handler.getStackInSlot(2).isEmpty()) {
                this.handler.setStackInSlot(2, currentRecipe.OUTPUT_1.copy());
            } else {
                this.handler.getStackInSlot(2).setCount(this.handler.getStackInSlot(2).getCount() + currentRecipe.OUTPUT_1.getCount());
            }
            this.resetRecipe();
        }
    }

    private boolean canStartCraft(ItemStack input, ClayWorkTableMethod method) {
        if (input.isEmpty()) {
            return false;
        }
        ClayWorkTableRecipes.Recipe recipe = ClayWorkTableRecipes.getRecipeFor(input, method);
        if (recipe.isEmpty()) {
            return false;
        }

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
            return (this.handler.getStackInSlot(2).isEmpty() || recipe.OUTPUT_1.isItemEqual(handler.getStackInSlot(2)))
                && recipe.OUTPUT_2.isItemEqual(this.handler.getStackInSlot(3))
                && recipe.OUTPUT_1.getCount() + this.handler.getStackInSlot(2).getCount() <= handler.getSlotLimit(2)
                && recipe.OUTPUT_2.getCount() + this.handler.getStackInSlot(3).getCount() <= handler.getSlotLimit(3);
        } else {
            return (this.handler.getStackInSlot(2).isEmpty() || recipe.OUTPUT_1.isItemEqual(handler.getStackInSlot(2)))
                && recipe.OUTPUT_1.getCount() + this.handler.getStackInSlot(2).getCount() <= handler.getSlotLimit(2);
        }
    }

    private void resetRecipe() {
        this.currentRecipe = ClayWorkTableRecipes.Recipe.EMPTY;
        this.requiredProgress = 0;
        this.craftingProgress = 0;
    }

    void resetRecipeIfEmptyInput() {
        if (this.handler.getStackInSlot(0).isEmpty()) {
            this.resetRecipe();
        }
    }
}
