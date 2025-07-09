package com.github.trc.clayium.api.util

import net.minecraft.util.math.BlockPos

class BlockPosIterator(
    first: BlockPos,
    last: BlockPos,
) : Iterator<BlockPos> {
    private val xRange = first.x..last.x
    private val zRange = first.z..last.z
    private val yRange = (first.y..last.y).reversed()

    private var xIter = xRange.iterator()
    private var zIter = zRange.iterator()
    private var yIter = yRange.iterator()

    override fun hasNext(): Boolean {
        return xIter.hasNext() || yIter.hasNext() || zIter.hasNext()
    }

    var lastY = yIter.next()
    var lastZ = zIter.next()

    override fun next(): BlockPos {
        return if (xIter.hasNext()) {
            BlockPos(xIter.next(), lastY, lastZ)
        } else if (zIter.hasNext()) {
            xIter = xRange.iterator()
            lastZ = zIter.next()
            BlockPos(xIter.next(), lastY, lastZ)
        } else if (yIter.hasNext()) {
            xIter = xRange.iterator()
            zIter = zRange.iterator()
            lastY = yIter.next()
            lastZ = zIter.next()
            BlockPos(xIter.next(), lastY, lastZ)
        } else {
            throw NoSuchElementException("No more elements in BlockPosIterator")
        }
    }
}