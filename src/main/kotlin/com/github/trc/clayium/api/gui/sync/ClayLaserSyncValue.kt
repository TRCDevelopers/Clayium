package com.github.trc.clayium.api.gui.sync

import com.cleanroommc.modularui.value.sync.GenericSyncValue
import com.github.trc.clayium.api.gui.sync.codec.ClayLaserAdapter
import com.github.trc.clayium.api.laser.ClayLaser
import java.util.function.Consumer
import java.util.function.Supplier

class ClayLaserSyncValue(
    getter: Supplier<ClayLaser?>,
    setter: Consumer<ClayLaser?>,
) : GenericSyncValue<ClayLaser?>(getter, setter, ClayLaserAdapter.INSTANCE)