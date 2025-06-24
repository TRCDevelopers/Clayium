package com.github.trc.clayium.api.capability

import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraftforge.common.util.INBTSerializable

/**
 * It is similar to the Predicate<ItemStack>, but it is impossible to serialize the nested lambda.
 */
interface IItemFilter : INBTSerializable<NBTTagCompound> {
    fun test(stack: ItemStack): Boolean

    // For nullability. There is no nullability annotation in the original `INBTSerializable` interface.
    override fun serializeNBT(): NBTTagCompound
    override fun deserializeNBT(nbt: NBTTagCompound)

    companion object {
        val ALWAYS_FALSE: IItemFilter = object : IItemFilter {
            override fun test(stack: ItemStack): Boolean = false

            override fun serializeNBT(): NBTTagCompound = NBTTagCompound()

            override fun deserializeNBT(nbt: NBTTagCompound) {}
        }
    }
}