package com.github.trcdeveloppers.clayium.common.util

object TierConstants {
    fun getTransferCooldon(tier: Int): Int {
        return when (tier) {
            1 -> 5
            2 -> 10
            3 -> 5
            in 4..13 -> 5
            else -> throw IllegalArgumentException("Invalid tier: $tier")
        }
    }
}