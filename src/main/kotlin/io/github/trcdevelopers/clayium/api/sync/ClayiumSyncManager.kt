package io.github.trcdevelopers.clayium.api.sync

import net.minecraft.network.PacketBuffer

class ClayiumSyncManager {
    private val properties = mutableListOf<ISyncedProperty>()

    fun <T: ISyncedProperty> register(property: T): T {
        if (properties.size >= 64) {
            throw IllegalStateException("Cannot register more than 64 synced properties. Consider creating another ClayiumSyncManager.")
        }
        properties.add(property)
        return property
    }

    fun write(buf: PacketBuffer) {
        var mask: Long = 0
        for ((i, p) in properties.withIndex()) {
            if (p.isDirty) {
                mask = mask or (1L shl i)
            }
        }
        buf.writeLong(mask)
        for (p in properties) {
            if (p.isDirty) {
                p.write(buf)
            }
        }
    }

    fun read(buf: PacketBuffer) {
        val mask = buf.readLong()
        for ((i, p) in properties.withIndex()) {
            if ((mask and (1L shl i)) != 0L) {
                p.read(buf)
            }
        }
    }

    fun integer(initial: Int): SyncedInt {
        return this.register(SyncedInt(initial))
    }

    fun boolean(initial: Boolean): SyncedBoolean {
        return this.register(SyncedBoolean(initial))
    }

    fun <E: Enum<E>> enum(enumClass: Class<E>, initial: E): SyncedEnum<E> {
        return this.register(SyncedEnum(enumClass, initial))
    }
}