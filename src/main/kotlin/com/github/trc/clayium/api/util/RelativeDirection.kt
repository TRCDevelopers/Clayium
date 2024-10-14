package com.github.trc.clayium.api.util

import net.minecraft.util.EnumFacing

enum class RelativeDirection(
    private val actualFacingGetter: (frontFacing: EnumFacing) -> EnumFacing
) {
    UP({ EnumFacing.UP }),
    DOWN({ EnumFacing.DOWN }),
    LEFT(EnumFacing::rotateY),
    RIGHT(EnumFacing::rotateYCCW),
    FRONT({ it }),
    BACK({ it.opposite });

    /**
     * @param frontFacing must be horizontal.
     * @throws IllegalArgumentException if frontFacing is not horizontal.
     */
    fun getActualFacing(frontFacing: EnumFacing): EnumFacing {
        require(frontFacing.axis.isHorizontal) { "Front facing must be horizontal" }
        return actualFacingGetter(frontFacing)
    }
}
