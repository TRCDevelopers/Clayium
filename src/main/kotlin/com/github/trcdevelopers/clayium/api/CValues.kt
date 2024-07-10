package com.github.trcdevelopers.clayium.api

import net.minecraftforge.fml.common.FMLCommonHandler
import net.minecraftforge.fml.relauncher.FMLLaunchHandler

object CValues {
    const val MOD_ID = "clayium"

    val isClient by lazy { FMLCommonHandler.instance().side.isClient }
    val isDeobf by lazy { FMLLaunchHandler.isDeobfuscatedEnvironment() }
}