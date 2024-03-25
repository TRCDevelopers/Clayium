package com.github.trcdevelopers.clayium.common.interfaces

import net.minecraft.client.resources.I18n
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly

interface ITiered {
    val tier: Int

    companion object {
        @SideOnly(Side.CLIENT)
        fun getTierTooltip(tier: Int): String {
            return I18n.format("tooltip.clayium.tier", tier)
        }
    }
}
