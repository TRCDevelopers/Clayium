package com.github.trcdevelopers.clayium.api

object CTranslation {
    const val CLAY_ENERGY = "tooltip.clayium.ce"
    const val MACHINE_TIER = "machine.clayium.tier"

    /**
     * @return translation key
     */
    fun tierPrefix(tier: Int): String {
        return "$MACHINE_TIER.$tier"
    }
}