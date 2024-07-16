package com.github.trc.clayium.api.capability

import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraftforge.common.util.INBTSerializable

/**
 * It is similar to the Predicate<ItemStack>, but it is impossible to serialize the nested lambda.
 */
interface IItemFilter : INBTSerializable<NBTTagCompound> {
    fun test(stack: ItemStack): Boolean
}