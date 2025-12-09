package io.github.trcdevelopers.clayium.api.sync

import net.minecraft.network.PacketBuffer

class SyncedBoolean(initial: Boolean) : ISyncedProperty {
    var value = initial
        set(value) {
            if (field != value) {
                this.isDirty = true
                field = value
            }
        }
    override var isDirty = false
        private set

    operator fun setValue(thisRef: Any?, property: kotlin.reflect.KProperty<*>, value: Boolean) {
        if (this.value != value) {
            this.isDirty = true
            this.value = value
        }
    }

    operator fun getValue(thisRef: Any?, property: kotlin.reflect.KProperty<*>): Boolean {
        return value
    }

    override fun write(buf: PacketBuffer) {
        buf.writeBoolean(this.value)
        this.isDirty = false
    }

    override fun read(buf: PacketBuffer) {
        this.value = buf.readBoolean()
    }
}