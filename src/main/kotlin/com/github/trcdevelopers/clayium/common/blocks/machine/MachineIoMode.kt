package com.github.trcdevelopers.clayium.common.blocks.machine

import com.google.common.collect.ImmutableList
import net.minecraft.util.IStringSerializable

enum class MachineIoMode(
    val id: Int,
) : IStringSerializable {
    NONE(0),
    SLOT_1(1),
    SLOT_2(2),
    ALL(3),
    CE(4),
    ;

    override fun getName(): String {
        return this.name.lowercase()
    }

    companion object {
        fun byId(id: Int): MachineIoMode {
            require(id in 0..4) { "Invalid id for MachineIoMode: $id" }
            return entries[id]
        }

        val defaultStateList = ImmutableList.of(NONE, NONE, NONE, NONE, NONE, NONE)
    }

    object Input {
        val BUFFER = listOf(NONE, ALL)
        val SINGLE = listOf(NONE, ALL, CE)
    }

    object Output {
        val BUFFER = listOf(NONE, ALL)
        val SINGLE = listOf(NONE, ALL)
    }
}