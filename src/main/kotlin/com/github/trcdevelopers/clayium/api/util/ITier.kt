package com.github.trcdevelopers.clayium.api.util

import net.minecraft.client.resources.I18n
import net.minecraftforge.common.IRarity
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly

interface ITier {
    val numeric: Int
    val prefixTranslationKey: String
    val rarity: IRarity

    companion object {
        @SideOnly(Side.CLIENT)
        fun tierNumericTooltip(tier: ITier) = I18n.format("tooltip.clayium.tier", tier.numeric)
    }
}