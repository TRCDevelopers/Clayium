package io.github.trcdevelopers.clayium.integration

import io.github.trcdevelopers.clayium.api.util.Mods
import io.github.trcdevelopers.clayium.integration.modularui.ModularUiInit
import io.github.trcdevelopers.clayium.integration.theoneprobe.TheOneProbeModule
import net.minecraftforge.fml.common.event.FMLInitializationEvent

object CModIntegration {
    fun init(event: FMLInitializationEvent) {
        // required mods
        ModularUiInit.init()

        // optional mods
        if (Mods.TheOneProbe.isModLoaded) {
            TheOneProbeModule.init()
        }
    }
}