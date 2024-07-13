package com.github.trc.clayium.api.metatileentity

import com.github.trc.clayium.api.metatileentity.interfaces.ISyncedTileEntity
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.network.PacketBuffer
import net.minecraft.util.EnumFacing
import net.minecraftforge.common.capabilities.Capability

abstract class MTETrait(
    protected val metaTileEntity: MetaTileEntity,
    val name: String,
) : ISyncedTileEntity {

    val tierNum = metaTileEntity.tier.numeric
    val networkId = idByName.computeIfAbsent(name) { nextId++ }

    init {
        metaTileEntity.addMetaTileEntityTrait(this)
    }

    open fun update() {}

    open fun serializeNBT(): NBTTagCompound = NBTTagCompound()
    open fun deserializeNBT(data: NBTTagCompound) {}

    override fun writeInitialSyncData(buf: PacketBuffer) {}
    override fun receiveInitialSyncData(buf: PacketBuffer) {}

    override fun writeCustomData(discriminator: Int, dataWriter: PacketBuffer.() -> Unit) {
        metaTileEntity.writeMteData(this, discriminator, dataWriter)
    }

    override fun receiveCustomData(discriminator: Int, buf: PacketBuffer) {}

    open fun <T> getCapability(capability: Capability<T>, facing: EnumFacing? = null): T? {
        return null
    }

    companion object {
        private val idByName = Object2IntOpenHashMap<String>().apply { defaultReturnValue(-1) }
        private var nextId = 0
    }
}