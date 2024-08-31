package com.github.trc.clayium.api.util

import codechicken.lib.vec.Cuboid6
import net.minecraft.util.math.BlockPos

/**
 * An iterator that iterates over all block positions within a Cuboid6 in a top-down order.
 *
 * **Uses MutableBlockPos. If you want to store the returned BlockPos for an extended period of time, call `.toImmutable()`.**
 */
class Cuboid6BlockPosIterator(
    cuboid6: Cuboid6,
) : Iterator<BlockPos.MutableBlockPos> {

    // x -> z -> y order
    private val xRange = cuboid6.min.x.toInt()..<cuboid6.max.x.toInt()
    private val zRange = cuboid6.min.z.toInt()..<cuboid6.max.z.toInt()
    private val yRange = (cuboid6.min.y.toInt()..<cuboid6.max.y.toInt()).reversed()

    private var xIter = xRange.iterator()
    private var zIter = zRange.iterator()
    private var yIter = yRange.iterator()

    private var lastX = xIter.next()
    private var lastZ = zIter.next()
    private var lastY = yIter.next()

    private val backingPos = BlockPos.MutableBlockPos()

    override fun hasNext(): Boolean {
        return yIter.hasNext() || xIter.hasNext() || zIter.hasNext()
    }

    override fun next(): BlockPos.MutableBlockPos {
        return if (xIter.hasNext()) {
            lastX = xIter.nextInt()
            backingPos.setPos(lastX, lastY, lastZ)
        } else if (zIter.hasNext()) {
            xIter = xRange.iterator()
            lastX = xIter.nextInt()
            lastZ = zIter.nextInt()
            backingPos.setPos(lastX, lastY, lastZ)
        } else if (yIter.hasNext()) {
            xIter = xRange.iterator()
            zIter = zRange.iterator()
            lastX = xIter.nextInt()
            lastZ = zIter.nextInt()
            lastY = yIter.nextInt()
            backingPos.setPos(lastX, lastY, lastZ)
        } else {
            throw NoSuchElementException()
        }
    }

    fun restart() {
        xIter = xRange.iterator()
        zIter = zRange.iterator()
        yIter = yRange.iterator()
        lastX = xIter.next()
        lastZ = zIter.next()
        lastY = yIter.next()
    }
}