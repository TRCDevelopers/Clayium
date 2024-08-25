package com.github.trc.clayium.api

import net.minecraftforge.fml.common.FMLCommonHandler
import net.minecraftforge.fml.relauncher.FMLLaunchHandler

object CValues {
    const val MOD_ID = "clayium"
    const val MOD_NAME = "Clayium"
    const val INV_TRANS_KEY = "container.inventory"

    const val HARDNESS_UNBREAKABLE = -1.0f

    val isClient by lazy { FMLCommonHandler.instance().side.isClient }
    val isDeobf by lazy { FMLLaunchHandler.isDeobfuscatedEnvironment() }
}