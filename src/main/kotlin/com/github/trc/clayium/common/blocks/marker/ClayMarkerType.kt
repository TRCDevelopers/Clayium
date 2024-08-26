package com.github.trc.clayium.common.blocks.marker

import net.minecraft.util.IStringSerializable

enum class ClayMarkerType : IStringSerializable {
    NO_EXTEND,
    EXTEND_TO_GROUND,
    EXTEND_TO_SKY,
    ALL_HEIGHT,
    ;

    override fun getName(): String {
        return name.lowercase()
    }
}