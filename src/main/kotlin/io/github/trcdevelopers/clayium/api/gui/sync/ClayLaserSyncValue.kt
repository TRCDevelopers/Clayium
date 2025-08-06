package io.github.trcdevelopers.clayium.api.gui.sync

import com.cleanroommc.modularui.value.sync.GenericSyncValue
import io.github.trcdevelopers.clayium.api.gui.sync.codec.ClayLaserByteBufAdapter
import io.github.trcdevelopers.clayium.api.laser.ClayLaser
import java.util.function.Consumer
import java.util.function.Supplier

class ClayLaserSyncValue(
    getter: Supplier<ClayLaser?>,
    setter: Consumer<ClayLaser?>,
) : GenericSyncValue<ClayLaser?>(getter, setter, ClayLaserByteBufAdapter.INSTANCE)