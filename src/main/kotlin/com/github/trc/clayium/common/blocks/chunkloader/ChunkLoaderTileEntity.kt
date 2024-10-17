package com.github.trc.clayium.common.blocks.chunkloader

import com.github.trc.clayium.api.MOD_NAME
import com.github.trc.clayium.api.util.CLog
import com.github.trc.clayium.common.ClayiumMod
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.math.ChunkPos
import net.minecraftforge.common.ForgeChunkManager

// todo modifiable via config or gui?
private const val SIZE = 1

class ChunkLoaderTileEntity : TileEntity() {
    var ticket: ForgeChunkManager.Ticket? = null

    fun onPlace() {
        val ticket = requestTicket() ?: return
        this.ticket = ticket
        forceChunkLoading(ticket)
    }

    fun onBreak() {
        releaseTicket()
    }

    fun requestTicket(): ForgeChunkManager.Ticket? {
        val ticket =
            ForgeChunkManager.requestTicket(ClayiumMod, this.world, ForgeChunkManager.Type.NORMAL)
        if (ticket == null) {
            CLog.warn(
                "Chunk Loader at $pos failed to request a new ticket. There are too many $MOD_NAME's Chunk Loaders in the world."
            )
            return null
        }
        ticket.modData.setLong("chunkLoaderPos", pos.toLong())
        return ticket
    }

    fun releaseTicket() {
        val ticket = ticket ?: return
        ForgeChunkManager.releaseTicket(ticket)
        this.ticket = null
    }

    fun forceChunkLoading(ticket: ForgeChunkManager.Ticket) {
        val centerChunk = ChunkPos(pos)
        for (dx in -SIZE..SIZE) {
            for (dz in -SIZE..SIZE) {
                val chunk = ChunkPos(centerChunk.x + dx, centerChunk.z + dz)
                ForgeChunkManager.forceChunk(ticket, chunk)
            }
        }
    }
}
