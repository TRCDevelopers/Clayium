package io.github.trcdevelopers.clayium.integration.theoneprobe

import io.github.trcdevelopers.clayium.integration.theoneprobe.providers.ClayEnergyInfoProvider
import io.github.trcdevelopers.clayium.integration.theoneprobe.providers.WorkableInfoProvider
import mcjty.theoneprobe.TheOneProbe

object TheOneProbeModule {
    fun init() {
        val top = TheOneProbe.theOneProbeImp
        top.registerProvider(ClayEnergyInfoProvider())
        top.registerProvider(WorkableInfoProvider())
    }
}