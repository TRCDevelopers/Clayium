package io.github.trcdevelopers.clayium.common.network

import io.github.trcdevelopers.clayium.common.util.KeyInput
import io.netty.buffer.ByteBuf
import it.unimi.dsi.fastutil.booleans.BooleanArrayList
import net.minecraftforge.fml.common.network.simpleimpl.IMessage

class KeyInputPacket() : IMessage {

    /**
     * Client Only
     */
    constructor(updating: List<KeyInput>) : this() {
        this.updating = updating
        this.data = BooleanArrayList().apply { updating.forEach { add(it.isKeyDown()) } }
    }

    lateinit var updating: List<KeyInput>
    lateinit var data: BooleanArrayList

    override fun toBytes(buf: ByteBuf) {
        buf.writeInt(updating.size)
        for (i in updating.indices) {
            buf.writeInt(updating[i].ordinal)
            buf.writeBoolean(data.getBoolean(i))
        }
    }

    override fun fromBytes(buf: ByteBuf) {
        val size = buf.readInt()
        val updating = mutableListOf<KeyInput>()
        val data = BooleanArrayList()
        repeat(size) {
            updating.add(KeyInput.entries[buf.readInt()])
            val isKeyDown = buf.readBoolean()
            data.add(isKeyDown)
        }
        this.updating = updating
        this.data = data
    }
}