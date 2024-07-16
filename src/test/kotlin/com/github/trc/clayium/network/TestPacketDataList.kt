package com.github.trc.clayium.network

import com.github.trc.clayium.Bootstrap
import com.github.trc.clayium.api.network.PacketDataList
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.collections.shouldContainAll
import io.kotest.matchers.shouldBe

class TestPacketDataList : StringSpec({

    beforeTest {
        Bootstrap.perform()
    }

    "Test PacketDataList" {
        val updates = PacketDataList()
        updates.add(0, byteArrayOf(0, 1, 2, 3))
        updates.add(1, byteArrayOf(4, 5, 6, 7))

        val tag = updates.dumpToNbt()
        tag.keySet.size shouldBe  2

        tag.keySet shouldContainAll setOf("0", "1")
        tag.getByteArray("0").forEachIndexed { i, byte ->
            byte shouldBe i.toByte()
        }
        tag.getByteArray("1").forEachIndexed { i, byte ->
            byte shouldBe (i + 4).toByte()
        }
    }
})