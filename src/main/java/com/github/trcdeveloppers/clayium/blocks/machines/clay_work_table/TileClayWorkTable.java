package com.github.trcdeveloppers.clayium.blocks.machines.clay_work_table;

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

import javax.annotation.Nullable;

public class TileClayWorkTable extends TileEntity {

    static final Capability<IItemHandler> ITEM_HANDLER_CAPABILITY = CapabilityItemHandler.ITEM_HANDLER_CAPABILITY;
    private final ItemStackHandler handler = new ItemStackHandler(4);

    int craftingProgress = 0;
    int requiredProgress = 0;
    ClayWorkTableMethod craftingMethod = ClayWorkTableMethod.ROLLING_HAND;

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        handler.deserializeNBT(compound);
        super.readFromNBT(compound);
    }
    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        compound.setTag("inventory", handler.serializeNBT());
        return super.writeToNBT(compound);
    }

    @Override
    public boolean hasCapability(Capability<?> capability, @Nullable EnumFacing facing) {
        return capability == ITEM_HANDLER_CAPABILITY || super.hasCapability(capability, facing);
    }

    @Nullable
    @Override
    public <T> T getCapability(Capability<T> capability, @Nullable EnumFacing facing) {
        return capability == ITEM_HANDLER_CAPABILITY ? (T) handler : super.getCapability(capability, facing);
    }

    private ItemStack getCurrentTool() {
        return this.handler.getStackInSlot(2);
    }

    @SideOnly(Side.CLIENT)
    int getCraftingProgressScaled(int scale) {
        if (this.requiredProgress == 0) {
            return 0;
        }
        return this.craftingProgress * scale / this.requiredProgress;
    }

    boolean canPushButton(int id) {
        switch (id) {
            case 3:
                return handler.getStackInSlot(3).isEmpty();
            default:
                return false;
        }
    }

    private boolean canStartCraft(ItemStack input, ClayWorkTableMethod method) {
        ClayWorkTableRecipes.ClayWorkTableRecipe recipe = ClayWorkTableRecipes.getRecipeFor(input, method);

        if (recipe == null) {
            return false;
        }

        switch (method) {
            // Does not require tool
            case ROLLING_HAND:
            case PUNCH:
                break;
            // Requires Rolling Pin
            case ROLLING_PIN:
                if (handler.getStackInSlot(1).getItem() != ClayiumItems.getItem("clay_rolling_pin")) {
                    return false;
                }
            // Requires Slicer or Spatula
            case CUT_PLATE:
            case CUT:
                if (!(handler.getStackInSlot(1).getItem() == ClayiumItems.getItem("clay_slicer")
                    || handler.getStackInSlot(1).getItem() == ClayiumItems.getItem("clay_spatula"))) {
                    return false;
                }
            // Requires Spatula
            case CUT_DISC:
                if (handler.getStackInSlot(1).getItem() != ClayiumItems.getItem("clay_spatula")) {
                    return false;
                }
        }

        if (recipe.hasSecondaryOutput()) {
            return recipe.OUTPUT_1.isItemEqual(handler.getStackInSlot(3))
                && recipe.OUTPUT_2.isItemEqual(handler.getStackInSlot(4))
                && recipe.OUTPUT_1.getCount() + handler.getStackInSlot(3).getCount() <= handler.getSlotLimit(3)
                && recipe.OUTPUT_2.getCount() + handler.getStackInSlot(4).getCount() <= handler.getSlotLimit(4);
        } else {
            return recipe.OUTPUT_1.isItemEqual(handler.getStackInSlot(3))
                && recipe.OUTPUT_1.getCount() + handler.getStackInSlot(3).getCount() <= handler.getSlotLimit(3);
        }

    }
}
