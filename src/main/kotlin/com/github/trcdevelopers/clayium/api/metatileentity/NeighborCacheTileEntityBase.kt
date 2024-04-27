package com.github.trcdevelopers.clayium.api.metatileentity

import net.minecraft.tileentity.TileEntity
import net.minecraft.util.EnumFacing
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import org.jetbrains.annotations.MustBeInvokedByOverriders
import kotlin.collections.fill

abstract class NeighborCacheTileEntityBase : SyncedTileEntityBase() {
    private val neighborCache = Array<TileEntity?>(6) { this }
    private var neighborsInvalidated = false

    init {
        invalidateNeighbors()
    }

    protected fun invalidateNeighbors() {
        if (!neighborsInvalidated) {
            neighborCache.fill(null)
            neighborsInvalidated = true
        }
    }

    @MustBeInvokedByOverriders
    override fun setWorld(worldIn: World) {
        super.setWorld(worldIn)
        invalidateNeighbors()
    }

    @MustBeInvokedByOverriders
    override fun setPos(posIn: BlockPos) {
        super.setPos(posIn)
        invalidateNeighbors()
    }

    @MustBeInvokedByOverriders
    override fun invalidate() {
        super.invalidate()
        invalidateNeighbors()
    }

    @MustBeInvokedByOverriders
    override fun onChunkUnload() {
        super.onChunkUnload()
        invalidateNeighbors()
    }

    fun getNeighbor(facing: EnumFacing): TileEntity? {
        if (world == null || pos == null) return null
        val i = facing.index
        var neighbor = neighborCache[i]
        if (neighbor == null || neighbor == this || (neighbor.isInvalid == true)) {
            neighbor = world.getTileEntity(pos.offset(facing))
            neighborCache[i] = neighbor
            neighborsInvalidated = false
        }
        return neighbor
    }

    open fun onNeighborChanged(facing: EnumFacing) {
        neighborCache[facing.index] = null
    }
}