package com.github.trc.clayium.api.util

import net.minecraft.client.resources.I18n
import net.minecraftforge.common.IRarity
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly

interface ITier : Comparable<ITier> {
    val numeric: Int
    val lowerName: String
    val prefixTranslationKey: String
    val rarity: IRarity

    abstract override fun hashCode(): Int
    override fun compareTo(other: ITier): Int = numeric.compareTo(other.numeric)

    companion object {
        @SideOnly(Side.CLIENT)
        fun tierNumericTooltip(tier: ITier) = I18n.format("tooltip.clayium.tier", tier.numeric)
    }
}