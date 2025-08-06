package io.github.trcdevelopers.clayium.api.metatileentity.interfaces

import net.minecraft.nbt.NBTTagCompound

/**
 * For [io.github.trcdevelopers.clayium.api.metatileentity.MetaTileEntity].
 *
 * If implemented, MTEs will be able to store arbitrary data to the ItemStack NBT.
 */
interface IHasItemStackNbt {

    /**
     * Called when a block is destroyed.
     *
     * Edit the given `NBTTagCompound` to store data.
     * The data will be written to the dropped ItemStack.
     */
    fun writeItemStackNbt(data: NBTTagCompound)

    /**
     * Called on a block placement.
     */
    fun readItemStackNbt(data: NBTTagCompound)
}