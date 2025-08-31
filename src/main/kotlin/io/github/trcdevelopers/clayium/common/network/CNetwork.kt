package io.github.trcdevelopers.clayium.common.network

import io.github.trcdevelopers.clayium.api.MOD_ID
import io.github.trcdevelopers.clayium.common.network.handlers.KeyInputPacketHandler
import net.minecraftforge.fml.common.network.NetworkRegistry
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper
import net.minecraftforge.fml.relauncher.Side

object CNetwork {
    val channel: SimpleNetworkWrapper = NetworkRegistry.INSTANCE.newSimpleChannel(MOD_ID)

    fun init() {
        var id = 0
        channel.registerMessage(KeyInputPacketHandler, KeyInputPacket::class.java, id++, Side.SERVER)
        channel.registerMessage(LastRecipePacket, LastRecipePacket::class.java, id++, Side.CLIENT)
    }
}