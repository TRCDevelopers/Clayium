package com.github.trcdeveloppers.clayium.blocks.machines.clay_work_table;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.*;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;

import javax.annotation.Nonnull;

public class ClayWorktableContainer extends Container {

    private final TileClayWorkTable tile;

    private int lastCraftingProgress = 0;
    private int lastRequiredProgress = 0;
    private ClayWorkTableMethod lastCraftingMethod = ClayWorkTableMethod.ROLLING_HAND;

    public ClayWorktableContainer(IInventory playerInv, TileClayWorkTable te) {
        this.tile = te;
        IItemHandler itemHandler = te.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null);
        addSlotToContainer(new SlotItemHandler(itemHandler, 0, 17, 30)); // input
        addSlotToContainer(new SlotItemHandler(itemHandler, 1, 80, 17)); // tool
        addSlotToContainer(new SlotItemHandler(itemHandler, 2, 143, 30) { // primary output
            @Override
            public boolean isItemValid(@Nonnull ItemStack stack) {
                return false;
            }
        });
        addSlotToContainer(new SlotItemHandler(itemHandler, 3, 143, 55) { // secondary output
            @Override
            public boolean isItemValid(@Nonnull ItemStack stack) {
                return  false;
            }
        });

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 9; j++) {
                addSlotToContainer(new Slot(playerInv,  j + i*9 + 9, 8 + j*18, 84 + i * 18));
            }
        }
        for (int i = 0; i < 9; i++) {
            addSlotToContainer(new Slot(playerInv, i, 8 + i * 18, 142));
        }
    }

    @Override
    public void addListener(IContainerListener listener) {
        super.addListener(listener);
//        listener.sendWindowProperty(this, 0, this.tileClayWorkTable.);
    }

    @Override
    public boolean canInteractWith(EntityPlayer playerIn) {
        return !playerIn.isSpectator();
    }

    @Override
    public ItemStack transferStackInSlot(EntityPlayer playerIn, int index) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = inventorySlots.get(index);
        if (slot != null && slot.getHasStack()) {
            ItemStack itemstack1 = slot.getStack();
            itemstack = itemstack1.copy();
            int containerSlots = inventorySlots.size() - playerIn.inventory.mainInventory.size();
            if (index < containerSlots) {
                if (!this.mergeItemStack(itemstack1, containerSlots, inventorySlots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.mergeItemStack(itemstack1, 0, containerSlots, false)) {
                return ItemStack.EMPTY;
            }
            if (itemstack1.getCount() == 0) {
                slot.putStack(ItemStack.EMPTY);
            } else {
                slot.onSlotChanged();
            }
            if (itemstack1.getCount() == itemstack.getCount()) {
                return ItemStack.EMPTY;
            }
            slot.onTake(playerIn, itemstack1);
        }
        return itemstack;
    }

    @Override
    public void detectAndSendChanges() {
        super.detectAndSendChanges();

        for (IContainerListener listener : this.listeners) {
            if (this.lastCraftingProgress != this.tile.craftingProgress) {
                listener.sendWindowProperty(this, 0, this.tile.craftingProgress);
            }
            if (this.lastRequiredProgress != this.tile.requiredProgress) {
                listener.sendWindowProperty(this, 1, this.tile.requiredProgress);
            }
            if (this.lastCraftingMethod != this.tile.craftingMethod) {
                listener.sendWindowProperty(this, 2, this.tile.craftingMethod.id);
            }
        }

        this.lastCraftingProgress = this.tile.craftingProgress;
        this.lastRequiredProgress = this.tile.requiredProgress;
        this.lastCraftingMethod = this.tile.craftingMethod;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void updateProgressBar(int id, int data) {
        switch (id) {
            case 0:
                this.tile.craftingProgress = data;
            case 1:
                this.tile.requiredProgress = data;
            case 2:
                this.tile.craftingMethod = ClayWorkTableMethod.fromId(data);
        }
    }
}
