package com.github.trc.clayium.api.network

import net.minecraft.nbt.NBTTagCompound
import net.minecraft.nbt.NBTTagList

class PacketDataList {
    private var discriminators = IntArray(4)
    private var data = ArrayList<ByteArray>(4)
    private var size = 0

    val isEmpty: Boolean
        get() = size == 0

    fun add(discriminator: Int, data: ByteArray) {
        if (size == discriminators.size) {
            this.discriminators = this.discriminators.copyOf(size * 2)
        }
        discriminators[size] = discriminator
        this.data.add(data)
        size++
    }

    fun dumpToNbt(): NBTTagCompound {
        val list = NBTTagList()
        val compound = NBTTagCompound()
        for (i in 0..<this.size) {
            compound.setByteArray(this.discriminators[i].toString(), this.data[i])
        }
        this.size = 0
        this.data.clear()
        this.discriminators.fill(0)
        return compound
    }
}