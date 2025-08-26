package io.github.trcdevelopers.clayium.api.recipe

import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.ResourceLocation
import net.minecraftforge.common.util.INBTSerializable
import net.minecraftforge.items.IItemHandler

interface IRecipeOutputs : INBTSerializable<NBTTagCompound> {
    val type: ResourceLocation

    fun canFit(outputInventory: IItemHandler): Boolean
    fun produceOutputs(outputInventory: IItemHandler)

    override fun serializeNBT(): NBTTagCompound
    override fun deserializeNBT(nbt: NBTTagCompound)
}