package com.github.trc.clayium.api.metatileentity

import com.github.trc.clayium.api.metatileentity.interfaces.ISyncedTileEntity
import com.github.trc.clayium.api.network.PacketDataList
import io.netty.buffer.Unpooled
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.network.NetworkManager
import net.minecraft.network.PacketBuffer
import net.minecraft.network.play.server.SPacketUpdateTileEntity
import net.minecraft.tileentity.TileEntity
import net.minecraftforge.common.util.Constants

abstract class SyncedTileEntityBase : TileEntity(), ISyncedTileEntity {
    private val updates = PacketDataList()

    override fun writeCustomData(discriminator: Int, dataWriter: PacketBuffer.() -> Unit) {
        if (world.isRemote) return
        val buf = Unpooled.buffer()
        dataWriter(PacketBuffer(buf))
        updates.add(discriminator, buf.array().copyOf(buf.writerIndex()))
        world?.notifyBlockUpdate(pos, world.getBlockState(pos), world.getBlockState(pos), 0)
    }

    override fun getUpdatePacket(): SPacketUpdateTileEntity? {
        if (this.updates.isEmpty) return null
        return SPacketUpdateTileEntity(pos, 0,
            NBTTagCompound().also { it.setTag("d", this.updates.dumpToNbt()) })
    }

    override fun onDataPacket(net: NetworkManager, pkt: SPacketUpdateTileEntity) {
        val discToDataPairsTagList = pkt.nbtCompound.getTagList("d", Constants.NBT.TAG_COMPOUND)
        for (entryNbtBase in discToDataPairsTagList) {
            val entry = entryNbtBase as NBTTagCompound
            for (discriminator in entry.keySet) {
                val data = entry.getByteArray(discriminator)
                receiveCustomData(discriminator.toInt(), PacketBuffer(Unpooled.wrappedBuffer(data)))
            }
        }
    }

    override fun getUpdateTag(): NBTTagCompound {
        val backedBuf = Unpooled.buffer()
        writeInitialSyncData(PacketBuffer(backedBuf))
        val updateData = backedBuf.array().copyOf(backedBuf.writerIndex())
        @Suppress("UsePropertyAccessSyntax") // `super.updateTag` errors with "unresolved reference"
        return super.getUpdateTag().also { it.setByteArray("d", updateData) }
    }

    override fun handleUpdateTag(tag: NBTTagCompound) {
        receiveInitialSyncData(PacketBuffer(Unpooled.wrappedBuffer(tag.getByteArray("d"))))
        super.handleUpdateTag(tag)
    }
}