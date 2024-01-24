package com.github.trcdevelopers.clayium.common.blocks.machine

import net.minecraft.util.IStringSerializable

enum class EnumIoMode(
    val index: Int
) : IStringSerializable {
    NONE(0),
    NORMAL(1),
    CE(2)
    ;

    val next
        get() = when (this) {
            NONE -> NORMAL
            NORMAL -> CE
            CE -> NONE
        }

    override fun getName(): String {
        return this.name.lowercase()
    }

    companion object {
        fun byIndex(index: Int): EnumIoMode {
            return when (index) {
                0 -> NONE
                1 -> NORMAL
                2 -> CE
                else -> throw IllegalArgumentException("Invalid index: $index")
            }
        }
    }
}