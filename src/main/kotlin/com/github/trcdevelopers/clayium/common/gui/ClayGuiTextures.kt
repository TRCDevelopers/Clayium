package com.github.trcdevelopers.clayium.common.gui

import com.cleanroommc.modularui.drawable.UITexture
import com.github.trcdevelopers.clayium.common.Clayium

object ClayGuiTextures {

    val LARGE_SLOT = UITexture.builder()
        .location(Clayium.MOD_ID, "gui/slot")
        .imageSize(256, 256)
        .uv(0, 32, 26, 26)
        .canApplyTheme()
        .build()

    val CLAY_SLOT = UITexture.builder()
        .location(Clayium.MOD_ID, "gui/slot")
        .imageSize(256, 256)
        .uv(96, 0, 18, 18)
        .build()

    val PROGRESS_BAR = UITexture.builder()
        .location(Clayium.MOD_ID, "gui/progress_bar")
        .imageSize(256, 256)
        .uv(1, 0, 22, 34)
        .canApplyTheme()
        .build()
}