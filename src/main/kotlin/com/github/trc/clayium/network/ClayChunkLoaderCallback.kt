package com.github.trc.clayium.network

import com.github.trc.clayium.common.blocks.chunkloader.ChunkLoaderTileEntity
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import net.minecraftforge.common.ForgeChunkManager

object ClayChunkLoaderCallback : ForgeChunkManager.LoadingCallback {
    override fun ticketsLoaded(tickets: List<ForgeChunkManager.Ticket>, world: World) {
        for (ticket in tickets) {
            val pos = BlockPos.fromLong(ticket.modData.getLong("chunkLoaderPos"))
            val chunkLoader = world.getTileEntity(pos)
            if (chunkLoader is ChunkLoaderTileEntity) {
                chunkLoader.ticket = ticket
                chunkLoader.forceChunkLoading(ticket)
            } else {
                ForgeChunkManager.releaseTicket(ticket)
            }
        }
    }
}
