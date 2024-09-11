package com.github.trc.clayium.integration.theoneprobe

import com.github.trc.clayium.integration.theoneprobe.providers.ClayEnergyInfoProvider
import com.github.trc.clayium.integration.theoneprobe.providers.RecipeLogicProvider
import com.github.trc.clayium.integration.theoneprobe.providers.WorkableInfoProvider
import mcjty.theoneprobe.TheOneProbe

object TheOneProbeModule {
    fun init() {
        val top = TheOneProbe.theOneProbeImp
        top.registerProvider(ClayEnergyInfoProvider())
        top.registerProvider(WorkableInfoProvider())
        top.registerProvider(RecipeLogicProvider())
    }
}