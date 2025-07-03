package com.github.trc.clayium.common.capability.impl

import com.github.trc.clayium.api.capability.IItemFilter
import com.github.trc.clayium.api.unification.OreDictUnifier
import com.github.trc.clayium.api.util.CUtils
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound

class ItemFilterFuzzy(
    private var stacks: List<ItemStack> = listOf(),
) : IItemFilter {
    override fun test(stack: ItemStack): Boolean {
        return stacks.any { filter ->
            val itemEqual = filter.isItemEqualIgnoreDurability(stack)
            if (itemEqual) return@any true
            val filterOreDicts = OreDictUnifier.getOreNames(filter)
            val stackOreDicts = OreDictUnifier.getOreNames(stack)
            val oreDictMatches = filterOreDicts.any { it in stackOreDicts }

            return@any oreDictMatches
        }
    }

    override fun serializeNBT(): NBTTagCompound {
        val nbt = NBTTagCompound()
        CUtils.writeItems(stacks, "stacks", nbt)
        return nbt
    }

    override fun deserializeNBT(nbt: NBTTagCompound) {
        this.stacks = CUtils.readItems("stacks", nbt)
    }
}