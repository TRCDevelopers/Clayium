package io.github.trcdevelopers.clayium.common.capability.impl

import io.github.trcdevelopers.clayium.api.capability.IItemFilter
import net.minecraft.nbt.NBTTagCompound

abstract class StringItemFilterBase(
    filter: String,
) : IItemFilter {
    protected var regex = createRegex(filter)

    protected open fun createRegex(filter: String): Regex {
        return filter.toRegex()
    }

    override fun serializeNBT(): NBTTagCompound {
        return NBTTagCompound().apply { setString("filter", regex.pattern) }
    }

    override fun deserializeNBT(nbt: NBTTagCompound) {
        val filter = nbt.getString("filter") ?: ""
        this.regex = createRegex(filter)
    }
}