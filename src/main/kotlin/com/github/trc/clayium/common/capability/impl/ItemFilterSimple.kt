package com.github.trc.clayium.common.capability.impl

import com.github.trc.clayium.api.capability.ClayiumCapabilities
import com.github.trc.clayium.api.capability.IItemFilter
import com.github.trc.clayium.api.util.CUtils
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound

class ItemFilterSimple(
    private var stacks: List<ItemStack> = listOf(),
    private var whitelist: Boolean = true,
) : IItemFilter {
    override fun test(stack: ItemStack): Boolean {
        @Suppress("IfThenToElvis") // simple if-then is more readable imo
        val match = stacks.any {
            val nestedFilter = it.getCapability(ClayiumCapabilities.ITEM_FILTER, null)
            if (nestedFilter != null) {
                nestedFilter.test(stack)
            } else {
                it.isItemEqual(stack) && ItemStack.areItemStackTagsEqual(it, stack)
            }
        }
        return match == whitelist
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

    override fun toString(): String {
        return "SimpleItemFilter(stacks=$stacks, whitelist=$whitelist)"
    }
}