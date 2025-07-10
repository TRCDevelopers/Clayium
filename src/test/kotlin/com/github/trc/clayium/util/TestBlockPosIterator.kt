package com.github.trc.clayium.util

import com.github.trc.clayium.Bootstrap
import com.github.trc.clayium.api.util.BlockPosIterator
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import net.minecraft.util.math.BlockPos

class TestBlockPosIterator : StringSpec({

    beforeTest {
        Bootstrap.perform()
    }

    "test only one" {
        val first = BlockPos.ORIGIN
        val last = BlockPos.ORIGIN
        val iter = BlockPosIterator(first, last)
        iter.hasNext() shouldBe true
        iter.next() shouldBe BlockPos.ORIGIN
        iter.hasNext() shouldBe false
    }

    "test 1 layer, 3x3" {
        val first = BlockPos.ORIGIN
        val last = BlockPos(2, 0, 2)
        val iter = BlockPosIterator(first, last)
        iter.hasNext() shouldBe true
        iter.next() shouldBe BlockPos(0, 0, 0)
        iter.hasNext() shouldBe true
        iter.next() shouldBe BlockPos(1, 0, 0)
        iter.hasNext() shouldBe true
        iter.next() shouldBe BlockPos(2, 0, 0)
        iter.hasNext() shouldBe true
        iter.next() shouldBe BlockPos(0, 0, 1)
        iter.hasNext() shouldBe true
        iter.next() shouldBe BlockPos(1, 0, 1)
        iter.hasNext() shouldBe true
        iter.next() shouldBe BlockPos(2, 0, 1)
        iter.hasNext() shouldBe true
        iter.next() shouldBe BlockPos(0, 0, 2)
        iter.hasNext() shouldBe true
        iter.next() shouldBe BlockPos(1, 0, 2)
        iter.hasNext() shouldBe true
        iter.next() shouldBe BlockPos(2, 0, 2)
        iter.hasNext() shouldBe false
    }

    "iterates from the sky" {
        val first = BlockPos(0, 0, 0)
        val last = BlockPos(1, 1, 1)
        val iter = BlockPosIterator(first, last)
        iter.next() shouldBe BlockPos(0, 1, 0)
        iter.next() shouldBe BlockPos(1, 1, 0)
        iter.next() shouldBe BlockPos(0, 1, 1)
        iter.next() shouldBe BlockPos(1, 1, 1)
        iter.next() shouldBe BlockPos(0, 0, 0)
        iter.next() shouldBe BlockPos(1, 0, 0)
        iter.next() shouldBe BlockPos(0, 0, 1)
        iter.next() shouldBe BlockPos(1, 0, 1)
    }

    "restart" {
        val iter = BlockPosIterator(BlockPos.ORIGIN, BlockPos.ORIGIN)
        iter.hasNext() shouldBe true
        iter.next() shouldBe BlockPos.ORIGIN
        iter.hasNext() shouldBe false
        iter.restart()
        iter.hasNext() shouldBe true
        iter.next() shouldBe BlockPos.ORIGIN
        iter.hasNext() shouldBe false
    }
})