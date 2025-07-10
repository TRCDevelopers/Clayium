package com.github.trc.clayium.api.metatileentity.interfaces

import net.minecraft.network.PacketBuffer

interface ISyncedTileEntity {

    /**
     * Used to sync data from Server -> Client.
     * Called during initial loading of the chunk or when many blocks change at once.
     *
     * This method is called on the **Server side**.
     * Data is received in [receiveInitialSyncData].
     *
     * This method is equivalent to [net.minecraft.tileentity.TileEntity.getUpdateTag]
     */
    fun writeInitialSyncData(buf: PacketBuffer)

    /**
     * Used to receive data from a Server.
     * Called during initial loading of the chunk or when many blocks change at once.
     *
     * This method is called on the **Client side**.
     * Data is sent from [writeInitialSyncData].
     *
     * This method is equivalent to [net.minecraft.tileentity.TileEntity.handleUpdateTag]
     */
    fun receiveInitialSyncData(buf: PacketBuffer)

    /**
     * Used to sync arbitrary data from Server -> Client.
     *
     * This method is called on the **Server side**.
     * Data is received in [receiveCustomData].
     *
     * Usage Example:
     * ```kotlin
     * import com.github.trc.clayium.api.capability.ClayiumDataCodecs.UPDATE_FRONT_FACING
     * // ...
     * writeCustomData(UPDATE_FRONT_FACING) {
     *     writeByte(value.index)
     * }
     * ```
     *
     * This method is equivalent to [net.minecraft.tileentity.TileEntity.getUpdatePacket]
     *
     * @see [com.github.trc.clayium.api.capability.ClayiumDataCodecs]
     */
    fun writeCustomData(discriminator: Int, dataWriter: PacketBuffer.() -> Unit)

    /**
     * Used to receive arbitrary data from a Server.
     *
     * This method is called on the **Client side**.
     * Data is sent from [writeCustomData].
     *
     * Example:
     * ```kotlin
     *
     * override fun receiveCustomData(discriminator: Int, buf: PacketBuffer) {
     *     when (discriminator) {
     *         UPDATE_FRONT_FACING -> {
     *             frontFacing = EnumFacing.byIndex(buf.readByte().toInt())
     *             this.scheduleRenderUpdate()
     *         }
     *     }
     * }
     * ```
     *
     * This method is equivalent to [net.minecraft.tileentity.TileEntity.onDataPacket]
     */
    fun receiveCustomData(discriminator: Int, buf: PacketBuffer)
}