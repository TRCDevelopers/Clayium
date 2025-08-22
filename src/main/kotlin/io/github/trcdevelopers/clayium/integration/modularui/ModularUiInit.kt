package io.github.trcdevelopers.clayium.integration.modularui

import com.cleanroommc.modularui.api.IThemeApi
import com.cleanroommc.modularui.utils.JsonBuilder

object ModularUiInit {

    const val CLAYIUM_DEFAULT_THEME = "clayium:default"

    fun init() {
        val theme = JsonBuilder()
            .add("parent", "DEFAULT")
            .add("openCloseAnimation", 0)
            .add("tooltipPos", "NEXT_TO_MOUSE")
        IThemeApi.get().registerTheme(CLAYIUM_DEFAULT_THEME, theme)
    }
}