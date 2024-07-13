package com.github.trc.clayium.api.metatileentity.interfaces

import net.minecraft.network.PacketBuffer

interface ISyncedTileEntity {

    fun writeInitialSyncData(buf: PacketBuffer)
    fun receiveInitialSyncData(buf: PacketBuffer)

    fun writeCustomData(discriminator: Int, dataWriter: PacketBuffer.() -> Unit)
    fun receiveCustomData(discriminator: Int, buf: PacketBuffer)
}