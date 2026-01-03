package io.github.trcdevelopers.clayium.integration.theoneprobe

import io.github.trcdevelopers.clayium.integration.theoneprobe.providers.ClayEnergyInfoProvider
import io.github.trcdevelopers.clayium.integration.theoneprobe.providers.AbstractWorkableInfoProvider
import io.github.trcdevelopers.clayium.integration.theoneprobe.providers.WorkableInfoProvider
import mcjty.theoneprobe.TheOneProbe

object TheOneProbeModule {
    // TOP Info Colors
    const val COLOR_ENABLED_ARGB: Int = 0xFF4CBB17.toInt()
    const val COLOR_DISABLED_ARGB: Int = 0xFFBB1C28.toInt()
    const val BORDER_COLOR: Int = 0xFF555555.toInt()
    const val HALF_HOUR_TICKS: Int = 30 * 60 * 20
    const val ONE_MIN_TICKS: Double = 60 * 20.0

    fun init() {
        val top = TheOneProbe.theOneProbeImp
        top.registerProvider(ClayEnergyInfoProvider())
        top.registerProvider(WorkableInfoProvider())
        top.registerProvider(AbstractWorkableInfoProvider())
    }
}