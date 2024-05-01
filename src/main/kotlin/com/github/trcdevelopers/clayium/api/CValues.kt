package com.github.trcdevelopers.clayium.api

import net.minecraftforge.fml.common.FMLCommonHandler

object CValues {
    const val MOD_ID = "clayium"

    private var isClient = false
    private var isClientCached = false

    fun isClientSide(): Boolean {
        if (!isClientCached) {
            isClient = FMLCommonHandler.instance().side.isClient
        }
        return isClient
    }
}