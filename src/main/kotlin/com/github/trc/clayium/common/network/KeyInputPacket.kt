package com.github.trc.clayium.common.network

import com.github.trc.clayium.common.util.KeyInput
import io.netty.buffer.ByteBuf
import net.minecraftforge.fml.common.network.simpleimpl.IMessage

class KeyInputPacket() : IMessage {

    /** Client Only */
    constructor(updating: List<KeyInput>) : this() {
        this.updating = updating
        this.data = updating.map { KeyInput.MutBooleanPairKeyData(it.isKeyDown(), it.isPressed()) }
    }

    lateinit var updating: List<KeyInput>
    lateinit var data: List<KeyInput.MutBooleanPairKeyData>

    override fun fromBytes(buf: ByteBuf) {
        val size = buf.readInt()
        val updating = mutableListOf<KeyInput>()
        val data = mutableListOf<KeyInput.MutBooleanPairKeyData>()
        (0..<size).forEach {
            updating.add(KeyInput.entries[buf.readInt()])
            val isKeyDown = buf.readBoolean()
            val isPressed = buf.readBoolean()
            data.add(KeyInput.MutBooleanPairKeyData(isKeyDown, isPressed))
        }
        this.updating = updating
        this.data = data
    }

    override fun toBytes(buf: ByteBuf) {
        buf.writeInt(updating.size)
        for (key in updating) {
            buf.writeInt(key.ordinal)
            buf.writeBoolean(key.isKeyDown())
            buf.writeBoolean(key.isPressed())
        }
    }
}
