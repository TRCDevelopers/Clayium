package com.github.trc.clayium.api.util

import com.github.trc.clayium.api.util.CUtils.clayiumId
import net.minecraft.item.EnumRarity
import net.minecraft.util.IStringSerializable
import net.minecraft.util.ResourceLocation
import net.minecraftforge.common.IRarity

enum class ClayTiers(
    override val numeric: Int,
    override val prefixTranslationKey: String,
    override val rarity: IRarity,
    val hullLocation: ResourceLocation = clayiumId("blocks/machinehull_tier$numeric"),
) : ITier, IStringSerializable {
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
    AZ91D(6, "tier.clayium.az91d", EnumRarity.UNCOMMON, clayiumId("blocks/az91d_hull")),
    ZK60A(6, "tier.clayium.zk60a", EnumRarity.UNCOMMON, clayiumId("blocks/zk60a_hull")),
    ;

    override val lowerName: String = name.lowercase()
    override fun getName(): String = lowerName
}