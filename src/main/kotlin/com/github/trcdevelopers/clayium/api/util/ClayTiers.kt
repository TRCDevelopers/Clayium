package com.github.trcdevelopers.clayium.api.util

import net.minecraft.item.EnumRarity
import net.minecraftforge.common.IRarity

enum class ClayTiers(
    override val numeric: Int,
    override val prefixTranslationKey: String,
    override val rarity: IRarity
) : ITier {
    DEFAULT(0, "tier.clayium.default", EnumRarity.COMMON),
    CLAY(1, "tier.clayium.clay", EnumRarity.COMMON),
    DENSE_CLAY(2, "tier.clayium.dense_clay", EnumRarity.COMMON),
    SIMPLE(3, "tier.clayium.simple", EnumRarity.COMMON),
    BASIC(4, "tier.clayium.basic", EnumRarity.UNCOMMON),
    ADVANCED(5, "tier.clayium.advanced", EnumRarity.UNCOMMON),
    PRECISION(6, "tier.clayium.precision", EnumRarity.UNCOMMON),
    CLAY_STEEL(7, "tier.clayium.clay_steel", EnumRarity.UNCOMMON),
    CLAYIUM(8, "tier.clayium.clayium", EnumRarity.RARE),
    ULTIMATE(9, "tier.clayium.ultimate", EnumRarity.RARE),
    ANTIMATTER(10, "tier.clayium.antimatter", EnumRarity.RARE),
    PURE_ANTIMATTER(11, "tier.clayium.pure_antimatter", EnumRarity.RARE),
    OEC(12, "tier.clayium.oec", EnumRarity.EPIC),
    OPA(13, "tier.clayium.opa", EnumRarity.EPIC),
    ;

    override val lowerName = name.lowercase()
}