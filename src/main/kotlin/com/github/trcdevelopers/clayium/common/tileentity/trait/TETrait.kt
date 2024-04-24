package com.github.trcdevelopers.clayium.common.tileentity.trait

import com.github.trcdevelopers.clayium.common.tileentity.TileEntityMachine
import net.minecraft.nbt.NBTTagCompound

abstract class TETrait(
    protected val tileEntity: TileEntityMachine,
    protected val tier: Int,
) {

    open fun update() {}

    open fun writeToNBT(data: NBTTagCompound) {}
    open fun readFromNBT(data: NBTTagCompound) {}
}