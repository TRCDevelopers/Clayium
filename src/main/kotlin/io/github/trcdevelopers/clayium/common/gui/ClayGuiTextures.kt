package io.github.trcdevelopers.clayium.common.gui

import com.cleanroommc.modularui.drawable.UITexture
import io.github.trcdevelopers.clayium.api.MOD_ID
import io.github.trcdevelopers.clayium.api.util.Mods
import io.github.trcdevelopers.clayium.api.util.clayiumId

object ClayGuiTextures {

    val LARGE_SLOT: UITexture = slotTexture()
        .xy(0, 32, 26, 26)
        .canApplyTheme()
        .build()

    val CLAY_SLOT: UITexture = slotTexture()
        .xy(96, 0, 18, 18)
        .build()

    val FILTER_SLOT: UITexture = slotTexture()
        .xy(96, 32, 18, 18)
        .build()

    // memo: gui/slot slot interval is 14 pixels
    val IMPORT_1_SLOT: UITexture = slotTexture()
        .xy(32, 0, 18, 18)
        .build()
    val IMPORT_2_SLOT: UITexture = slotTexture()
        .xy(32, 32, 18, 18)
        .build()

    val EXPORT_1_SLOT: UITexture = slotTexture()
        .xy(64, 0, 18, 18)
        .build()
    val EXPORT_2_SLOT: UITexture = slotTexture()
        .xy(64, 32, 18, 18)
        .build()

    /* Multi-trac buffer Slots */
    val SLOT_M1: UITexture = slotTexture().xy(32, 96, 18, 18).build()
    val SLOT_M2: UITexture = slotTexture().xy(64, 96, 18, 18).build()
    val SLOT_M3: UITexture = slotTexture().xy(96, 96, 18, 18).build()
    val SLOT_M4: UITexture = slotTexture().xy(128, 96, 18, 18).build()
    val SLOT_M5: UITexture = slotTexture().xy(160, 96, 18, 18).build()
    val SLOT_M6: UITexture = slotTexture().xy(192, 96, 18, 18).build()

    val M_TRACK_SLOTS = arrayOf(SLOT_M1, SLOT_M2, SLOT_M3, SLOT_M4, SLOT_M5, SLOT_M6)

    val FILTER_SLOT_M1: UITexture = slotTexture().xy(32, 128, 18, 18).build()
    val FILTER_SLOT_M2: UITexture = slotTexture().xy(64, 128, 18, 18).build()
    val FILTER_SLOT_M3: UITexture = slotTexture().xy(96, 128, 18, 18).build()
    val FILTER_SLOT_M4: UITexture = slotTexture().xy(128, 128, 18, 18).build()
    val FILTER_SLOT_M5: UITexture = slotTexture().xy(160, 128, 18, 18).build()
    val FILTER_SLOT_M6: UITexture = slotTexture().xy(192, 128, 18, 18).build()

    val M_TRACK_FILTER_SLOTS = arrayOf(FILTER_SLOT_M1, FILTER_SLOT_M2, FILTER_SLOT_M3, FILTER_SLOT_M4, FILTER_SLOT_M5, FILTER_SLOT_M6)

    val PROGRESS_BAR: UITexture = UITexture.builder()
        .location(clayiumId("gui/progress_bar"))
        .imageSize(256, 256)
        .xy(1, 0, 22, 34)
        .canApplyTheme()
        .build()

    // GuiTextures.MC_BUTTON_PRESSED is bugged
    val BUTTON_PRESSED: UITexture = UITexture.builder()
            .location(Mods.ModularUI.modId, "gui/widgets/mc_button")
            .imageSize(16, 32)
            .xy(0, 16, 16, 16)
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

    val REPEAT_DISABLED = button(64, 0)
    val REPEAT = button(64, 16)
    val REPEAT_HOVERED = button(64, 32)
    //endregion

    object Clicker {
        val BLOCK_DISABLED = button(80, 0)
        val BLOCK = button(80, 16)
        val BLOCK_HOVERED = button(80, 32)

        val ENTITY_DISABLED = button(96, 0)
        val ENTITY = button(96, 16)
        val ENTITY_HOVERED = button(96, 32)

        val BLOCK_AND_ENTITY_DISABLED = button(112, 0)
        val BLOCK_AND_ENTITY = button(112, 16)
        val BLOCK_AND_ENTITY_HOVERED = button(112, 32)

        val FIXED_TARGET_DISABLED = button(128, 0)
        val FIXED_TARGET = button(128, 16)
        val FIXED_TARGET_HOVERED = button(128, 32)

        val RAYTRACE_DISABLED = button(144, 0)
        val RAYTRACE = button(144, 16)
        val RAYTRACE_HOVERED = button(144, 32)

        val NO_SNEAK_DISABLED = button(160, 0)
        val NO_SNEAK = button(160, 16)
        val NO_SNEAK_HOVERED = button(160, 32)

        val SNEAK_DISABLED = button(176, 0)
        val SNEAK = button(176, 16)
        val SNEAK_HOVERED = button(176, 32)
    }

    object WorkTable {
        val ROLLING_HAND = ButtonUiTextures(clayiumId("gui/clay_work_table_icons"), 80, 0)
        val PUNCH = ButtonUiTextures(clayiumId("gui/clay_work_table_icons"), 80 + 16, 0)
        val ROLLING_PIN = ButtonUiTextures(clayiumId("gui/clay_work_table_icons"), 80 + 32, 0)
        val CUT_PLATE = ButtonUiTextures(clayiumId("gui/clay_work_table_icons"), 80 + 48, 0)
        val CUT_DISC = ButtonUiTextures(clayiumId("gui/clay_work_table_icons"), 80 + 64, 0)
        val CUT = ButtonUiTextures(clayiumId("gui/clay_work_table_icons"), 80 + 80, 0)
        val LIST = arrayOf(ROLLING_HAND, PUNCH, ROLLING_PIN, CUT_PLATE, CUT_DISC, CUT)

        val PROGRESS_BAR_EMPTY: UITexture = UITexture.builder()
            .location(clayiumId("gui/clay_work_table_icons"))
            .imageSize(256, 256)
            .xy(0, 0, 80, 32)
            .canApplyTheme()
            .build()
    }

    private fun slotTexture(): UITexture.Builder = UITexture.builder()
        .location(MOD_ID, "gui/slot")
        .imageSize(256, 256)

    private fun button(u: Int, v: Int): UITexture = UITexture.builder()
        .location(MOD_ID, "gui/button")
        .imageSize(256, 256)
        .xy(u, v, 16, 16)
        .build()
}