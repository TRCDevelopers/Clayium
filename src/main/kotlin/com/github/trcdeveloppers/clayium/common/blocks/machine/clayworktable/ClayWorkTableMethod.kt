package com.github.trcdeveloppers.clayium.common.blocks.machine.clayworktable

enum class ClayWorkTableMethod(val id: Int) {
    ROLLING_HAND(1),
    PUNCH(2),
    ROLLING_PIN(3),
    CUT_PLATE(4),
    CUT_DISC(5),
    CUT(6),
    ;

    companion object {
        @JvmStatic
        fun fromId(id: Int): ClayWorkTableMethod? {
            return when (id) {
                1 -> ROLLING_HAND
                2 -> PUNCH
                3 -> ROLLING_PIN
                4 -> CUT_PLATE
                5 -> CUT_DISC
                6 -> CUT
                else -> null
            }
        }
    }
}
