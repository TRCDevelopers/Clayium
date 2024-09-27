package com.github.trc.clayium.common.gui

import com.cleanroommc.modularui.drawable.UITexture
import com.github.trc.clayium.api.MOD_ID
import com.github.trc.clayium.api.util.Mods
import com.github.trc.clayium.api.util.clayiumId

object ClayGuiTextures {

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
        .location(clayiumId("gui/progress_bar"))
        .imageSize(256, 256)
        .uv(1, 0, 22, 34)
        .canApplyTheme()
        .build()

    // GuiTextures.MC_BUTTON_PRESSED is bugged
    val BUTTON_PRESSED = UITexture.builder()
            .location(Mods.ModularUI.modId, "gui/widgets/mc_button")
            .imageSize(16, 32)
            .uv(0, 16, 16, 16)
            .name("mc_button_hovered")
            .build()

    //region Buttons
    val CE_BUTTON_DISABLED = button(0, 0)
    val CE_BUTTON = button(0, 16)
    val CE_BUTTON_HOVERED = button(0, 32)

    val START_BUTTON_DISABLED = button(16, 0)
    val START_BUTTON = button(16, 16)
    val START_BUTTON_HOVERED = button(16, 32)

    val STOP_BUTTON_DISABLED = button(32, 0)
    val STOP_BUTTON = button(32, 16)
    val STOP_BUTTON_HOVERED = button(32, 32)

    val DISPLAY_RANGE_DISABLED = button(48, 0)
    val DISPLAY_RANGE = button(48, 16)
    val DISPLAY_RANGE_HOVERED = button(48, 32)

    val RESET_DISABLED = button(64, 0)
    val RESET = button(64, 16)
    val RESET_HOVERED = button(64, 32)
    //endregion

    private fun slotTexture() = UITexture.builder()
        .location(MOD_ID, "gui/slot")
        .imageSize(256, 256)

    private fun button(u: Int, v: Int) = UITexture.builder()
        .location(MOD_ID, "gui/button")
        .imageSize(256, 256)
        .uv(u, v, 16, 16)
        .build()
}