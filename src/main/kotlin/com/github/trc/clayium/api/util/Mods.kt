package com.github.trc.clayium.api.util

import net.minecraftforge.fml.common.Loader

enum class Mods(
    val modId: String,
) {

    AE2(Names.AE2),
    EnderIO(Names.ENDER_IO),
    JustEnoughItems(Names.JUST_ENOUGH_ITEMS),
    ModularUI(Names.MODULAR_UI),
    ;

    val isModLoaded by lazy { Loader.isModLoaded(this.modId) }

    object Names {
        const val AE2 = "appliedenergistics2"
        const val ENDER_IO = "enderio"
        const val JUST_ENOUGH_ITEMS = "jei"
        const val MODULAR_UI = "modularui"
    }
}