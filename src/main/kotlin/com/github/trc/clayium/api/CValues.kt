package com.github.trc.clayium.api

import net.minecraftforge.fml.common.FMLCommonHandler
import net.minecraftforge.fml.relauncher.FMLLaunchHandler

// todo replace CValues.MOD_ID to this
const val MOD_ID = "clayium"
const val MOD_NAME = "Clayium"

const val GUI_DEFAULT_WIDTH = 176
const val GUI_DEFAULT_HEIGHT = 166

object CValues {
    const val MOD_ID = "clayium"
    const val MOD_NAME = "Clayium"
    const val INV_TRANS_KEY = "container.inventory"

    const val HARDNESS_UNBREAKABLE = -1.0f

    const val MIN_WORLD_BUILD_HEIGHT = 0
    const val MAX_WORLD_BUILD_HEIGHT = 255

    val isClient by lazy { FMLCommonHandler.instance().side.isClient }
    val isDeobf by lazy { FMLLaunchHandler.isDeobfuscatedEnvironment() }
}