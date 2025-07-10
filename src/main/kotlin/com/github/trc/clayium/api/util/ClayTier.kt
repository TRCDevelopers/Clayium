package com.github.trc.clayium.api.util

import net.minecraft.item.EnumRarity
import net.minecraftforge.common.IRarity

data class ClayTier(
    override val numeric: Int,
    override val rarity: IRarity,
    override val lowerName: String,
) : ITier {
    override val prefixTranslationKey: String = "tier.clayium.$lowerName"

    companion object {
        fun zk60a(num: Int, rarity: IRarity = defaultRarity(num)) = ClayTier(num, rarity, "zk60a")

        fun defaultRarity(num: Int): IRarity {
            return when (num) {
                in 0..3 -> EnumRarity.COMMON
                in 4..7 -> EnumRarity.UNCOMMON
                in 8..11 -> EnumRarity.RARE
                in 12..13 -> EnumRarity.EPIC
                else -> EnumRarity.COMMON
            }
        }
    }
}