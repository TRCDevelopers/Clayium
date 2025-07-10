package com.github.trc.clayium.common.gui.sync

import com.cleanroommc.modularui.value.sync.ValueSyncHandler
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.network.PacketBuffer
import net.minecraft.village.MerchantRecipeList
import java.util.function.Consumer
import java.util.function.Supplier

class MerchantRecipeListSyncValue(
    private val getter: Supplier<MerchantRecipeList>,
    private val setter: Consumer<MerchantRecipeList>,
) : ValueSyncHandler<MerchantRecipeList>() {

    private var cache: MerchantRecipeList? = null
    private var cacheNbt: List<NBTTagCompound>? = null

    override fun setValue(value: MerchantRecipeList, setSource: Boolean, sync: Boolean) {
        this.cache = value
        this.cacheNbt = value.map { it.writeToTags() }
        if (setSource) {
            this.setter.accept(value)
        }
        if (sync) {
            this.sync(0, this::write)
        }
    }

    override fun updateCacheFromSource(isFirstSync: Boolean): Boolean {
        val value = this.getter.get()
        val valueNbt = value.map { it.writeToTags() }
        val cacheNbt = this.cacheNbt
        val isEqual = cacheNbt != null && valueNbt.zip(cacheNbt).all { (t1, t2) -> t1 == t2 }
        if (isFirstSync || !isEqual) {
            setValue(value, setSource = false, sync = false)
        }
        return !isEqual
    }

    override fun write(buffer: PacketBuffer) {
        this.cache!!.writeToBuf(buffer)
    }

    override fun read(buffer: PacketBuffer) {
        this.setValue(MerchantRecipeList.readFromBuf(buffer), setSource = true, sync = false)
    }

    override fun getValue(): MerchantRecipeList? {
        return this.cache
    }
}