package com.github.trcdevelopers.clayium.common.util

import net.minecraft.nbt.NBTTagCompound

object NBTTypeUtils {

    const val NBT_BYTE_ID = 1
    const val NBT_SHORT_ID = 2
    const val NBT_INT_ID = 3
    const val NBT_LONG_ID = 4
    const val NBT_FLOAT_ID = 5
    const val NBT_DOUBLE_ID = 6
    const val NBT_BYTE_ARRAY_ID = 7
    const val NBT_STRING_ID = 8
    const val NBT_COMPOUND_ID = 10
    const val NBT_INT_ARRAY_ID = 11
    const val NBT_LONG_ARRAY_ID = 12

    fun NBTTagCompound.hasByte(key: String) = this.hasKey(key, 1)
    fun NBTTagCompound.hasShort(key: String) = this.hasKey(key, 2)
    fun NBTTagCompound.hasInt(key: String) = this.hasKey(key, 3)
    fun NBTTagCompound.hasLong(key: String) = this.hasKey(key, 4)
    fun NBTTagCompound.hasFloat(key: String) = this.hasKey(key, 5)
    fun NBTTagCompound.hasDouble(key: String) = this.hasKey(key, 6)
    fun NBTTagCompound.hasByteArray(key: String) = this.hasKey(key, 7)
    fun NBTTagCompound.hasString(key: String) = this.hasKey(key, 8)
    fun NBTTagCompound.hasCompound(key: String) = this.hasKey(key, 10)
    fun NBTTagCompound.hasIntArray(key: String) = this.hasKey(key, 11)
    fun NBTTagCompound.hasLongArray(key: String) = this.hasKey(key, 12)

}