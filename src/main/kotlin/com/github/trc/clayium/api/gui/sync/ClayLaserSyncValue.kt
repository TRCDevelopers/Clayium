package com.github.trc.clayium.api.gui.sync

import com.cleanroommc.modularui.utils.serialization.IByteBufAdapter
import com.cleanroommc.modularui.value.sync.GenericSyncValue
import com.github.trc.clayium.api.laser.ClayLaser
import com.github.trc.clayium.api.laser.readClayLaser
import com.github.trc.clayium.api.laser.writeClayLaser
import com.google.common.base.Supplier
import net.minecraft.network.PacketBuffer
import java.util.function.Consumer

class ClayLaserSyncValue(
    getter: Supplier<ClayLaser>,
    setter: Consumer<ClayLaser>,
) : GenericSyncValue<ClayLaser>(getter, setter, ClayLaserCodec) {
    object ClayLaserCodec : IByteBufAdapter<ClayLaser> {
        override fun deserialize(buffer: PacketBuffer): ClayLaser {
            return buffer.readClayLaser()
        }

        override fun serialize(buffer: PacketBuffer, u: ClayLaser) {
            buffer.writeClayLaser(u)
        }

        override fun areEqual(t1: ClayLaser, t2: ClayLaser): Boolean {
            return t1 == t2
        }
    }
}