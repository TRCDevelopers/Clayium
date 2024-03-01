package com.github.trcdevelopers.clayium.common.blocks.machine.single_single

import net.minecraft.nbt.NBTTagCompound
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.EnumFacing
import net.minecraftforge.common.capabilities.Capability
import net.minecraftforge.items.CapabilityItemHandler.ITEM_HANDLER_CAPABILITY
import net.minecraftforge.items.ItemStackHandler

class TileSingleSingle(
    var tier: Int = -1,
) : TileEntity() {

    private val itemHandler = object : ItemStackHandler(2) {
        override fun onContentsChanged(slot: Int) {
            this@TileSingleSingle.markDirty()
        }
    }

    private fun initParams(tier: Int) {
        this.tier = tier
    }

    override fun writeToNBT(compound: NBTTagCompound): NBTTagCompound {
        compound.setTag("Inventory", itemHandler.serializeNBT())
        compound.setInteger("Tier", tier)
        return super.writeToNBT(compound)
    }

    override fun readFromNBT(compound: NBTTagCompound) {
        itemHandler.deserializeNBT(compound.getCompoundTag("Inventory"))
        initParams(compound.getInteger("Tier"))
        super.readFromNBT(compound)
    }

    override fun hasCapability(capability: Capability<*>, facing: EnumFacing?): Boolean {
        return capability === ITEM_HANDLER_CAPABILITY || super.hasCapability(capability, facing)
    }

    @Suppress("UNCHECKED_CAST")
    override fun <T> getCapability(capability: Capability<T>, facing: EnumFacing?): T? {
        return if (capability === ITEM_HANDLER_CAPABILITY) itemHandler as T else super.getCapability(capability, facing)
    }
}