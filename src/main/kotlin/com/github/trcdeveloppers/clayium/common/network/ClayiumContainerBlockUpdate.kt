package com.github.trcdeveloppers.clayium.common.network

import com.github.trcdeveloppers.clayium.common.blocks.machine.EnumIoMode
import io.netty.buffer.ByteBuf
import net.minecraft.util.EnumFacing
import net.minecraft.util.math.BlockPos
import net.minecraftforge.fml.common.network.simpleimpl.IMessage
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext

class ClayiumContainerBlockUpdate : IMessage {

    private lateinit var pos: BlockPos
    private lateinit var facing: EnumFacing
    private lateinit var ioMode: EnumIoMode

    override fun toBytes(buf: ByteBuf) {
        buf.writeLong(pos.toLong())
        buf.writeInt(facing.index)
        buf.writeInt(ioMode.ordinal)
    }

    override fun fromBytes(buf: ByteBuf) {
        this.pos = BlockPos.fromLong(buf.readLong())
        this.facing = EnumFacing.byIndex(buf.readInt())
        this.ioMode = EnumIoMode.byIndex(buf.readInt())
    }

    class ClayiumContainerBlockUpdateHandler : IMessageHandler<ClayiumContainerBlockUpdate, IMessage> {
        override fun onMessage(message: ClayiumContainerBlockUpdate, ctx: MessageContext): IMessage? {
            return null
        }
    }
}