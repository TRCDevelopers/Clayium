package com.github.trc.clayium.common.capability.impl

import com.github.trc.clayium.api.capability.IItemFilter
import net.minecraft.nbt.NBTTagCompound

abstract class StringItemFilterBase(
    private var filter: String,
) : IItemFilter {
    open val regex = filter.toRegex()

    override fun serializeNBT(): NBTTagCompound {
        return NBTTagCompound().apply { setString("filter", filter) }
    }

    override fun deserializeNBT(nbt: NBTTagCompound) {
        this.filter = nbt.getString("filter") ?: ""
    }
}