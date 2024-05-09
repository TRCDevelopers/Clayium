package com.github.trcdevelopers.clayium.common.gui

import com.cleanroommc.modularui.drawable.UITexture
import com.github.trcdevelopers.clayium.api.CValues
import com.github.trcdevelopers.clayium.common.Clayium

object ClayGuiTextures {

    private const val SLOT_LOCATION = "${CValues.MOD_ID}:gui/slot"

    private fun slotTexture() = UITexture.builder()
        .location(CValues.MOD_ID, "gui/slot")
        .imageSize(256, 256)

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

    // memo: gui/slot slot interval is 14 pixels
    val IMPORT_1_SLOT = slotTexture()
        .uv(32, 0, 18, 18)
        .build()
    val IMPORT_2_SLOT = slotTexture()
        .uv(32, 32, 18, 18)
        .build()

    val EXPORT_1_SLOT = slotTexture()
        .uv(64, 0, 18, 18)
        .build()
    val EXPORT_2_SLOT = slotTexture()
        .uv(64, 32, 18, 18)
        .build()

    val PROGRESS_BAR = UITexture.builder()
        .location(Clayium.MOD_ID, "gui/progress_bar")
        .imageSize(256, 256)
        .uv(1, 0, 22, 34)
        .canApplyTheme()
        .build()
}