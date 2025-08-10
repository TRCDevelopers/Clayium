package io.github.trcdevelopers.clayium.common.network

import io.github.trcdevelopers.clayium.common.gui.ContainerClayCraftingBoard
import io.netty.buffer.ByteBuf
import net.minecraft.client.Minecraft
import net.minecraft.item.crafting.CraftingManager
import net.minecraft.item.crafting.IRecipe
import net.minecraft.util.ResourceLocation
import net.minecraftforge.fml.common.network.ByteBufUtils
import net.minecraftforge.fml.common.network.simpleimpl.IMessage
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext

// Packet Class must have a no-arg constructor
class LastRecipePacket @JvmOverloads constructor(
    var recipe: IRecipe? = null,
) : IMessage {

    override fun fromBytes(buf: ByteBuf) {
        if (buf.readBoolean()) {
            val rl = ResourceLocation(ByteBufUtils.readUTF8String(buf))
            this.recipe = CraftingManager.getRecipe(rl)
        } else {
            this.recipe = null
        }
    }

    override fun toBytes(buf: ByteBuf) {
        val recipe = this.recipe
        if (recipe == null) {
            buf.writeBoolean(false)
        } else {
            buf.writeBoolean(true)
            ByteBufUtils.writeUTF8String(buf, recipe.registryName.toString())
        }
    }

    companion object : IMessageHandler<LastRecipePacket, IMessage> {
        override fun onMessage(message: LastRecipePacket, ctx: MessageContext): IMessage? {
            val container = Minecraft.getMinecraft().player.openContainer as? ContainerClayCraftingBoard
                ?: return null
            container.lastRecipe = message.recipe
            return null
        }
    }
}