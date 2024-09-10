package com.github.trc.clayium.api.capability.impl

import net.minecraft.nbt.NBTTagCompound
import net.minecraftforge.common.util.INBTSerializable
import net.minecraftforge.energy.EnergyStorage

class EnergyStorageSerializable @JvmOverloads constructor(
    capacity: Int,
    maxReceive: Int,
    maxExtract: Int,
    energy: Int = 0,
) : EnergyStorage(capacity, maxReceive, maxExtract, energy), INBTSerializable<NBTTagCompound> {
    @JvmOverloads
    constructor(capacity: Int, maxTransfer: Int = capacity)
            : this(capacity, maxTransfer, maxTransfer)

    override fun serializeNBT(): NBTTagCompound {
        val data = NBTTagCompound()
        data.setInteger("energy", this.energy)
        return data
    }

    override fun deserializeNBT(nbt: NBTTagCompound) {
        this.energy = nbt.getInteger("energy")
    }
}