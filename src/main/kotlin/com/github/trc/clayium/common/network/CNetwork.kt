package com.github.trc.clayium.common.network

import com.github.trc.clayium.api.MOD_ID
import com.github.trc.clayium.common.network.handlers.KeyInputPacketHandler
import net.minecraftforge.fml.common.network.NetworkRegistry
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper
import net.minecraftforge.fml.relauncher.Side

object CNetwork {
    val channel: SimpleNetworkWrapper = NetworkRegistry.INSTANCE.newSimpleChannel(MOD_ID)

    fun init() {
        var id = 0
        channel.registerMessage(KeyInputPacketHandler, KeyInputPacket::class.java, id++, Side.SERVER)
    }
}