package com.github.trcdeveloppers.clayium.common.blocks.machine

import net.minecraft.util.IStringSerializable

enum class EnumImportMode : IStringSerializable {
    NONE,
    NORMAL,
    CE
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
}