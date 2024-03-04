package com.github.trcdevelopers.clayium.common.blocks.machine

import com.google.common.collect.Iterators
import net.minecraft.util.IStringSerializable

enum class EnumInputMode(
    val id: Int
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
        fun byId(id: Int): EnumInputMode {
            require(id in 0..4) { "Invalid id for EnumInputMode: $id" }
            return entries[id]
        }

        val BUFFER = Iterators.cycle(NONE, ALL)
        val MACHINE_SINGLE = Iterators.cycle(NONE, ALL, CE)
        val MACHINE_DOUBLE = Iterators.cycle(entries)
    }
}