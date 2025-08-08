package io.github.trcdevelopers.clayium.common.network

import io.netty.buffer.ByteBuf
import net.minecraft.item.crafting.IRecipe
import net.minecraftforge.fml.common.network.ByteBufUtils
import net.minecraftforge.fml.common.network.simpleimpl.IMessage
import net.minecraftforge.fml.common.registry.GameRegistry

class LastRecipePacket(
    var recipe: IRecipe
) : IMessage {

    override fun fromBytes(buf: ByteBuf) {
        this.recipe = ByteBufUtils.readRegistryEntry(buf, GameRegistry.findRegistry(IRecipe::class.java))
    }

    override fun toBytes(buf: ByteBuf) {
        ByteBufUtils.writeRegistryEntry(buf, recipe)
    }
}