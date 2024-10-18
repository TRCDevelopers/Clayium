package com.github.trc.clayium.api.capability

import com.github.trc.clayium.api.laser.ClayLaser
import net.minecraft.util.EnumFacing

interface IClayLaserSource {
    /** The laser that is irradiating. Null if deactivated. */
    val irradiatingLaser: ClayLaser?
    val direction: EnumFacing
    val length: Int
}
