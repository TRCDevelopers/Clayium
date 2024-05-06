package com.github.trcdevelopers.clayium.api.metatileentity

import com.github.trcdevelopers.clayium.api.metatileentity.interfaces.ISyncedTileEntity
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap
import net.minecraft.network.PacketBuffer

abstract class MTETrait(
    protected val metaTileEntity: MetaTileEntity,
    val name: String,
) : ISyncedTileEntity {

    val tier = metaTileEntity.tier
    val networkId = idByName.computeIfAbsent(name) { nextId++ }

    init {
        metaTileEntity.addMetaTileEntityTrait(this)
    }

    open fun update() {}

    override fun writeInitialSyncData(buf: PacketBuffer) {}
    override fun receiveInitialSyncData(buf: PacketBuffer) {}

    override fun writeCustomData(discriminator: Int, dataWriter: PacketBuffer.() -> Unit) {
        metaTileEntity.writeMteData(this, discriminator, dataWriter)
    }

    override fun receiveCustomData(discriminator: Int, buf: PacketBuffer) {}

    companion object {
        private val idByName = Object2IntOpenHashMap<String>().apply { defaultReturnValue(-1) }
        private var nextId = 0
    }
}