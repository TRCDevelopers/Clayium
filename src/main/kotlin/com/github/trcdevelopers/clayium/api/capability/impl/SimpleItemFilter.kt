package com.github.trcdevelopers.clayium.api.capability.impl

import com.github.trcdevelopers.clayium.api.capability.IItemFilter
import com.github.trcdevelopers.clayium.api.util.CUtils
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound

class SimpleItemFilter(
    private var stacks: List<ItemStack> = listOf(),
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
        this.stacks = CUtils.readItems("stacks", nbt)
        this.whitelist = nbt.getBoolean("whitelist")
    }
}