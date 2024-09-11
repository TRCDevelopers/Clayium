package com.github.trc.clayium.integration.theoneprobe

import com.github.trc.clayium.integration.theoneprobe.providers.ClayEnergyInfoProvider
import mcjty.theoneprobe.TheOneProbe

object TheOneProbeModule {
    fun init() {
        val top = TheOneProbe.theOneProbeImp
        top.registerProvider(ClayEnergyInfoProvider())
    }
}