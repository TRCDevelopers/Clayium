package com.github.trcdeveloppers.clayium.blocks.machines.clay_work_table;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;

import javax.annotation.Nullable;

public class TileClayWorkTable extends TileEntity {
    static final Capability<IItemHandler> ITEM_HANDLER_CAPABILITY = CapabilityItemHandler.ITEM_HANDLER_CAPABILITY;
    private final ItemStackHandler handler = new ItemStackHandler(4);



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
}
