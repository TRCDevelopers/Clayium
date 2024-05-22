package com.github.trcdevelopers.clayium.api.util

import net.minecraftforge.common.IRarity

interface ITier {
    val numeric: Int
    val prefixTranslationKey: String
    val rarity: IRarity
}