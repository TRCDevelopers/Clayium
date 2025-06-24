package com.github.trc.clayium.api.capability.impl

import com.github.trc.clayium.api.capability.IItemFilter
import com.github.trc.clayium.api.unification.OreDictUnifier
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound

class OreDictionaryItemFilter(
    private var oreName: String = "",
) : IItemFilter {

    val regex = oreName.toRegex()

    override fun test(stack: ItemStack): Boolean {
        return OreDictUnifier.getOreNames(stack).any { regex.matches(it) }
    }

    override fun serializeNBT(): NBTTagCompound {
        return NBTTagCompound().apply {
            setString("oreName", oreName)
        }
    }

    override fun deserializeNBT(nbt: NBTTagCompound) {
        this.oreName = nbt.getString("oreName") ?: ""
    }

}