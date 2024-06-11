package com.github.trcdevelopers.clayium.api.capability.impl

import com.github.trcdevelopers.clayium.api.capability.IItemFilter
import com.github.trcdevelopers.clayium.api.util.CUtils
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound

class SimpleItemFilter(
    private val stacks: MutableList<ItemStack> = mutableListOf(),
    private var whitelist: Boolean = true,
) : IItemFilter {
    override fun test(stack: ItemStack): Boolean {
        return stacks.any { it.isItemEqual(stack) } == whitelist
    }

    override fun serializeNBT(): NBTTagCompound {
        return NBTTagCompound().apply {
            CUtils.writeItems(stacks, "stacks", this)
            setBoolean("whitelist", whitelist)
        }
    }

    override fun deserializeNBT(nbt: NBTTagCompound) {
        stacks.clear()
        CUtils.readItems(stacks, "stacks", nbt)
        whitelist = nbt.getBoolean("whitelist")
    }
}