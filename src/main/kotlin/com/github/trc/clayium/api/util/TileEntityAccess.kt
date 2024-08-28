package com.github.trc.clayium.api.util

import net.minecraft.tileentity.TileEntity
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import java.lang.ref.WeakReference

/**
 * holds tile entity as a weak reference.
 *
 * if you get non-null from the [get] method, it ensures the following:
 *  - is valid
 *  - has the same position as [pos]
 *
 * if you get non-null from the [getIfLoaded] method, it also ensures the pos is loaded.
 */
class TileEntityAccess(
    val world: World,
    val pos: BlockPos,
) {

    constructor(tileEntity: TileEntity) : this(tileEntity.world, tileEntity.pos)

    private var weakRef: WeakReference<TileEntity?> = WeakReference(null)

    fun get(): TileEntity? {
        val prev = weakRef.get()
        val thisTime = if (isTileEntityValid(prev)) {
            prev
        } else {
            world.getTileEntity(pos)
        }
        weakRef = WeakReference(thisTime)
        return thisTime
    }

    fun getIfLoaded(): TileEntity? {
        val prev = weakRef.get()
        val thisTime = if (!world.isBlockLoaded(pos)) {
            null
        } else if (isTileEntityValid(prev)) {
            prev
        } else {
            world.getTileEntity(pos)
        }
        weakRef = WeakReference(thisTime)
        return thisTime
    }

    private fun isTileEntityValid(te: TileEntity?): Boolean {
        return te != null && !te.isInvalid && te.pos == this.pos
    }
}