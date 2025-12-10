package io.github.trcdevelopers.clayium.api.sync

import net.minecraft.network.PacketBuffer

class ClayiumSyncManager {
    private val properties = mutableListOf<ISyncedProperty>()
    private var dirtyMask: Int = 0

    val isDirty get() = dirtyMask != 0

    fun <T: ISyncedProperty> register(property: T): T {
        if (properties.size >= 32) {
            throw IllegalStateException("Cannot register more than 32 synced properties. Consider creating another ClayiumSyncManager.")
        }
        properties.add(property)
        return property
    }

    fun markDirty(index: Int) {
        dirtyMask = dirtyMask or (1 shl index)
    }

    fun write(buf: PacketBuffer) {
        if (this.dirtyMask == 0) {
            throw IllegalStateException("No properties are dirty, nothing to write.")
        }

        var mask = dirtyMask
        buf.writeInt(mask)
        while (mask != 0) {
            val i = Integer.numberOfTrailingZeros(mask)
            properties[i].write(buf)
            mask = mask and (1 shl i).inv()
        }
    }

    fun read(buf: PacketBuffer) {
        val mask = buf.readInt()
        for ((i, p) in properties.withIndex()) {
            if ((mask and (1 shl i)) != 0) {
                p.read(buf)
            }
        }
    }

    fun integer(initial: Int): SyncedInt {
        return this.register(SyncedInt(this, properties.size, initial))
    }

    fun boolean(initial: Boolean): SyncedBoolean {
        return this.register(SyncedBoolean(initial))
    }

    fun <E: Enum<E>> enum(enumClass: Class<E>, initial: E): SyncedEnum<E> {
        return this.register(SyncedEnum(enumClass, initial))
    }
}