package com.github.trc.clayium.common.network.handlers

import com.github.trc.clayium.common.network.KeyInputPacket
import net.minecraftforge.fml.common.network.simpleimpl.IMessage
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext

object KeyInputPacketHandler : IMessageHandler<KeyInputPacket, IMessage> {
    override fun onMessage(message: KeyInputPacket, ctx: MessageContext): IMessage? {
        val player = ctx.serverHandler.player
        player.serverWorld.addScheduledTask {
            for ((key, data) in message.updating.zip(message.data)) {
                key.update(player, data.isKeyDown, data.isPressed)
            }
        }
        return null
    }
}
