package io.github.trcdevelopers.clayium.integration

import io.github.trcdevelopers.clayium.api.util.Mods
import io.github.trcdevelopers.clayium.integration.theoneprobe.TheOneProbeModule
import net.minecraftforge.fml.common.event.FMLInitializationEvent

object CModIntegration {
    fun init(event: FMLInitializationEvent) {
        if (Mods.TheOneProbe.isModLoaded) {
            TheOneProbeModule.init()
        }
    }
}