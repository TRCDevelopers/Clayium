package com.github.trc.clayium.common.gui

import com.cleanroommc.modularui.drawable.UITexture
import com.github.trc.clayium.api.CValues
import com.github.trc.clayium.common.Clayium

object ClayGuiTextures {
    private fun slotTexture() = UITexture.builder()
        .location(CValues.MOD_ID, "gui/slot")
        .imageSize(256, 256)

    val LARGE_SLOT = slotTexture()
        .uv(0, 32, 26, 26)
        .canApplyTheme()
        .build()

    val CLAY_SLOT = slotTexture()
        .uv(96, 0, 18, 18)
        .build()

    val FILTER_SLOT = slotTexture()
        .uv(96, 32, 18, 18)
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

    /* Multi-trac buffer Slots */
    val SLOT_M1 = slotTexture().uv(32, 96, 18, 18).build()
    val SLOT_M2 = slotTexture().uv(64, 96, 18, 18).build()
    val SLOT_M3 = slotTexture().uv(96, 96, 18, 18).build()
    val SLOT_M4 = slotTexture().uv(128, 96, 18, 18).build()
    val SLOT_M5 = slotTexture().uv(160, 96, 18, 18).build()
    val SLOT_M6 = slotTexture().uv(192, 96, 18, 18).build()

    val M_TRACK_SLOTS = arrayOf(SLOT_M1, SLOT_M2, SLOT_M3, SLOT_M4, SLOT_M5, SLOT_M6)

    val FILTER_SLOT_M1 = slotTexture().uv(32, 128, 18, 18).build()
    val FILTER_SLOT_M2 = slotTexture().uv(64, 128, 18, 18).build()
    val FILTER_SLOT_M3 = slotTexture().uv(96, 128, 18, 18).build()
    val FILTER_SLOT_M4 = slotTexture().uv(128, 128, 18, 18).build()
    val FILTER_SLOT_M5 = slotTexture().uv(160, 128, 18, 18).build()
    val FILTER_SLOT_M6 = slotTexture().uv(192, 128, 18, 18).build()

    val M_TRACK_FILTER_SLOTS = arrayOf(FILTER_SLOT_M1, FILTER_SLOT_M2, FILTER_SLOT_M3, FILTER_SLOT_M4, FILTER_SLOT_M5, FILTER_SLOT_M6)

    val PROGRESS_BAR = UITexture.builder()
        .location(Clayium.MOD_ID, "gui/progress_bar")
        .imageSize(256, 256)
        .uv(1, 0, 22, 34)
        .canApplyTheme()
        .build()
}