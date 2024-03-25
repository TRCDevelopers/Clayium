package com.github.trcdevelopers.clayium.common.blocks.machine

import com.google.common.collect.ImmutableList
import net.minecraft.util.IStringSerializable

enum class MachineIoMode(
    val id: Int,
) : IStringSerializable {
    NONE(0),
    FIRST(1),
    SECOND(2),
    ALL(3),
    CE(4),
    M_ALL(5),
    M_1(6),
    M_2(7),
    M_3(8),
    M_4(9),
    M_5(10),
    M_6(11),
    ;

    override fun getName(): String {
        return this.name.lowercase()
    }

    companion object {
        fun byId(id: Int): MachineIoMode {
            return entries[id]
        }

        val defaultStateList: ImmutableList<MachineIoMode> = ImmutableList.of(NONE, NONE, NONE, NONE, NONE, NONE)
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