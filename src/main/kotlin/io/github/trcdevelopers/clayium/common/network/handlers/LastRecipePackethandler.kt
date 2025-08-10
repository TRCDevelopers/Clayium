package io.github.trcdevelopers.clayium.common.network.handlers

import io.github.trcdevelopers.clayium.common.gui.ContainerClayCraftingBoard
import io.github.trcdevelopers.clayium.common.network.LastRecipePacket
import net.minecraftforge.fml.common.network.simpleimpl.IMessage
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext

object LastRecipePackethandler : IMessageHandler<LastRecipePacket, IMessage> {
    override fun onMessage(message: LastRecipePacket, ctx: MessageContext): IMessage? {
        val container = ctx.serverHandler.player.openContainer as? ContainerClayCraftingBoard
            ?: return null
        return null
    }
}