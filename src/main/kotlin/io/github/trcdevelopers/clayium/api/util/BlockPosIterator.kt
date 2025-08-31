package io.github.trcdevelopers.clayium.api.util

import net.minecraft.util.math.BlockPos
import kotlin.math.max
import kotlin.math.min

/**
 * Iterates a square constructed from given block poses.
 * The iteration order is x -> z -> y.
 * x, z are iterated in ascending order, while y is iterated in descending order.
 */
class BlockPosIterator(first: BlockPos, last: BlockPos) : AbstractIterator<BlockPos>() {

    constructor(pair: Pair<BlockPos, BlockPos>) : this(pair.first, pair.second)

    private val min = BlockPos(min(first.x, last.x), min(first.y, last.y), min(first.z, last.z))
    private val max = BlockPos(max(first.x, last.x), max(first.y, last.y), max(first.z, last.z))

    private var x = min.x
    private var y = max.y
    private var z = min.z

    private var init = true

    override fun computeNext() {
        if (this.init) {
            this.init = false
            return this.setNext(BlockPos(this.x, this.y, this.z))
        }

        if (this.x < max.x) {
            this.x++
        } else if (this.z < max.z) {
            this.x = min.x
            this.z++
        } else if (this.y > min.y) {
            this.x = min.x
            this.z = min.z
            this.y--
        } else {
            return done()
        }
        setNext(BlockPos(this.x, this.y, this.z))
    }

    fun restart() {
        this.x = min.x
        this.y = max.y
        this.z = min.z
        this.init = true
        this.computeNext()
    }
}