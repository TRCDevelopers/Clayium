package com.github.trc.clayium.api.capability

import com.github.trc.clayium.api.laser.IClayLaser
import net.minecraft.util.EnumFacing

interface IClayLaserSource {
    /**
     * The laser that is irradiating.
     * Null if deactivated.
     */
    val irradiatingLaser: IClayLaser?
    val direction: EnumFacing
    val length: Int
}