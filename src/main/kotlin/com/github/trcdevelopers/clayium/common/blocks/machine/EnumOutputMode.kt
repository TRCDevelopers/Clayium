package com.github.trcdevelopers.clayium.common.blocks.machine

import com.google.common.collect.Iterators
import net.minecraft.util.IStringSerializable

enum class EnumOutputMode(
    val id: Int
) : IStringSerializable {
    NONE(0),
    SLOT_1(1),
    SLOT_2(2),
    ALL(3),
    ;

    override fun getName(): String {
        return this.name.lowercase()
    }

    companion object {
        fun byId(id: Int): EnumOutputMode {
            require(id in 0..3) { "Invalid id for EnumOutputMode: $id" }
            return entries[id]
        }

        val BUFFER = Iterators.cycle(NONE, ALL)
        val MACHINE_SINGLE = Iterators.cycle(NONE, ALL)
        val MACHINE_DOUBLE = Iterators.cycle(entries)
    }
}