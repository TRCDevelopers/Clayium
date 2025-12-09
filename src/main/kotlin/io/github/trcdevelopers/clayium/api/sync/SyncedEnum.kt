package io.github.trcdevelopers.clayium.api.sync

import net.minecraft.network.PacketBuffer

/**
 * Uses ordinal internally.
 */
class SyncedEnum<E: Enum<E>>(private val enumClass: Class<E>, initial: E) : ISyncedProperty {

    var value = initial
        set(value) {
            if (value != field) {
                this.isDirty = true
                field = value
            }
        }
    override var isDirty: Boolean = false
        private set

    operator fun setValue(thisRef: Any?, property: kotlin.reflect.KProperty<*>, value: E) {
        if (this.value != value) {
            this.isDirty = true
            this.value = value
        }
    }

    operator fun getValue(thisRef: Any?, property: kotlin.reflect.KProperty<*>): E {
        return value
    }

    override fun write(buf: PacketBuffer) {
        buf.writeEnumValue(this.value)
        this.isDirty = false
    }

    override fun read(buf: PacketBuffer) {
        this.value = buf.readEnumValue(enumClass)
    }
}
