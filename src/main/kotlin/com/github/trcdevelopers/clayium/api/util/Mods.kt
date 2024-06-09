package com.github.trcdevelopers.clayium.api.util

import net.minecraftforge.fml.common.Loader

enum class Mods(
    val modId: String,
) {

    JustEnoughItems(Names.JUST_ENOUGH_ITEMS)
    ;

    val isModLoaded by lazy { Loader.isModLoaded(this.modId) }

    object Names {
        const val JUST_ENOUGH_ITEMS = "jei"
    }
}