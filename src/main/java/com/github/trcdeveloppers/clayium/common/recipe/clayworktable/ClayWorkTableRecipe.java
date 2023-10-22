package com.github.trcdeveloppers.clayium.common.recipe.clayworktable;

import com.github.trcdeveloppers.clayium.Clayium;
import com.github.trcdeveloppers.clayium.common.blocks.machines.clayworktable.ClayWorkTableMethod;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.registries.IForgeRegistryEntry;

public class ClayWorkTableRecipe extends IForgeRegistryEntry.Impl<ClayWorkTableRecipe> {
    private final ItemStack input;
    private final ItemStack primaryOutput;
    private final ItemStack secondaryOutput;
    public final ClayWorkTableMethod method;
    private final int clicks;

    public ClayWorkTableRecipe(ItemStack input, ItemStack primaryOutput, ItemStack secondaryOutput, ClayWorkTableMethod method, int clicks) {
        if (clicks < 1) {
            Clayium.LOGGER.warn("ClayWorkTable Recipe must have positive integer in clicks but got {}. Correcting it to 1.", clicks);
            clicks = 1;
        }

        Clayium.LOGGER.info("RECIPE ITEM: {}", input);
        this.input = input;
        this.primaryOutput = primaryOutput;
        this.secondaryOutput = secondaryOutput;
        this.method = method;
        this.clicks = clicks;
    }
    public ClayWorkTableRecipe(ItemStack input, ItemStack primaryOutput, ClayWorkTableMethod method, int clicks) {
        this(input, primaryOutput, ItemStack.EMPTY, method, clicks);
    }

    public int getRequiredProcess() {
        return this.clicks;
    }

    public boolean hasSecondaryOutput() {
        return !this.secondaryOutput.isEmpty();
    }
    
    public boolean matches(ItemStack input, ClayWorkTableMethod method) {
        return this.method == method
            && this.input.isItemEqual(input)
            && this.input.getCount() <= input.getCount();
    }

    public ItemStack getInput() {
        return this.input;
    }

    public ItemStack getPrimaryOutput() {
        return this.primaryOutput.copy(); 
    }
    
    public ItemStack getSecondaryOutput() {
        return this.secondaryOutput.copy();
    }

    public boolean canOutputPrimary(Slot target) {
        return target.getHasStack() || this.primaryOutput.getCount() + target.getStack().getCount() <= target.getSlotStackLimit();
    }

    public boolean canOutputSecondary(Slot target) {
        return target.getHasStack() || this.secondaryOutput.getCount() + target.getStack().getCount() <= target.getSlotStackLimit();
    }
}
