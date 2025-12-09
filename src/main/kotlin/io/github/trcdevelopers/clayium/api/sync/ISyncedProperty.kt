package io.github.trcdevelopers.clayium.api.sync

import net.minecraft.network.PacketBuffer

/**
 * An interface for properties that can be synchronized between server and client.
 */
interface ISyncedProperty {
    val isDirty: Boolean

    fun write(buf: PacketBuffer)
    fun read(buf: PacketBuffer)
}