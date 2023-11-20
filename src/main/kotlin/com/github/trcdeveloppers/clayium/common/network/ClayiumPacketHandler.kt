package com.github.trcdeveloppers.clayium.common.network

import com.github.trcdeveloppers.clayium.Clayium
import net.minecraftforge.fml.common.network.NetworkRegistry
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper

object ClayiumPacketHandler {
    val handler: SimpleNetworkWrapper = NetworkRegistry.INSTANCE.newSimpleChannel(Clayium.MOD_ID)
}