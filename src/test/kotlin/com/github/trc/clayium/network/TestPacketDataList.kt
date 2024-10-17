package com.github.trc.clayium.network

import com.github.trc.clayium.Bootstrap
import com.github.trc.clayium.api.network.PacketDataList
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.collections.shouldContainAll
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.nbt.NBTTagList

class TestPacketDataList :
    StringSpec({
        beforeTest { Bootstrap.perform() }

        "Test PacketDataList" {
            val updates = PacketDataList()
            updates.add(0, byteArrayOf(0, 1, 2, 3))
            updates.add(1, byteArrayOf(4, 5, 6, 7))
            updates.add(1, byteArrayOf(1))

            val tagList: NBTTagList = updates.dumpToNbt()
            tagList.shouldHaveSize(3)

            for ((i, entryBase) in tagList.withIndex()) {
                val entry = entryBase as NBTTagCompound
                for (discriminator in entry.keySet) {
                    when (i) {
                        0 -> {
                            discriminator.toInt() shouldBe 0
                            entry.getByteArray(discriminator).toList() shouldContainAll
                                byteArrayOf(0, 1, 2, 3).toList()
                        }
                        1 -> {
                            discriminator.toInt() shouldBe 1
                            entry.getByteArray(discriminator).toList() shouldContainAll
                                byteArrayOf(4, 5, 6, 7).toList()
                        }
                        2 -> {
                            discriminator.toInt() shouldBe 1
                            entry.getByteArray(discriminator).toList() shouldContainAll
                                byteArrayOf(1).toList()
                        }
                    }
                }
            }
        }
    })
