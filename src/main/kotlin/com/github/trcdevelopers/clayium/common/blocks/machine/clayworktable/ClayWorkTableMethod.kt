package com.github.trcdevelopers.clayium.common.blocks.machine.clayworktable

enum class ClayWorkTableMethod(val id: Int) {
    ROLLING_HAND(0),
    PUNCH(1),
    ROLLING_PIN(2),
    CUT_PLATE(3),
    CUT_DISC(4),
    CUT(5),
    ;

    companion object {
        @JvmStatic
        fun fromId(id: Int): ClayWorkTableMethod? {
            return when (id) {
                0 -> ROLLING_HAND
                1 -> PUNCH
                2 -> ROLLING_PIN
                3 -> CUT_PLATE
                4 -> CUT_DISC
                5 -> CUT
                else -> null
            }
        }
    }
}
