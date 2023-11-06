package com.github.trcdeveloppers.clayium.common.blocks.machine

import net.minecraft.util.IStringSerializable

enum class EnumInsertionModeSingle : IStringSerializable {
    NONE,
    INSERTION,
    INSERTION_CE
    ;

    val next
        get() = when (this) {
            NONE -> INSERTION
            INSERTION -> INSERTION_CE
            INSERTION_CE -> NONE
        }

    override fun getName(): String {
        return this.name.lowercase()
    }
}