package com.github.trc.clayium.common.blocks.marker

import com.github.trc.clayium.api.util.ClayTiers
import net.minecraft.util.IStringSerializable

enum class ClayMarkerType(
    val tier: ClayTiers
) : IStringSerializable {
    NO_EXTEND(ClayTiers.CLAY_STEEL),
    EXTEND_TO_GROUND(ClayTiers.CLAYIUM),
    EXTEND_TO_SKY(ClayTiers.CLAYIUM),
    ALL_HEIGHT(ClayTiers.CLAYIUM),
    ;

    override fun getName(): String {
        return name.lowercase()
    }
}