package com.github.trc.clayium.integration

import com.github.trc.clayium.api.util.Mods
import com.github.trc.clayium.integration.theoneprobe.TheOneProbeModule
import net.minecraftforge.fml.common.event.FMLInitializationEvent

object CModIntegration {
    fun init(event: FMLInitializationEvent) {
        if (Mods.TheOneProbe.isModLoaded) {
            TheOneProbeModule.init()
        }
    }
}
