package io.github.trcdevelopers.clayium.common.gui

import com.cleanroommc.modularui.api.drawable.IKey
import com.cleanroommc.modularui.screen.viewport.ModularGuiContext
import com.cleanroommc.modularui.theme.WidgetTheme
import com.cleanroommc.modularui.theme.WidgetThemeEntry
import com.cleanroommc.modularui.widgets.TextWidget

class ResizingTextWidget(key: IKey) : TextWidget<ResizingTextWidget>(key) {
    override fun draw(context: ModularGuiContext, widgetTheme: WidgetThemeEntry<*>) {
        val resizer = this.resizer()
        resizer.setWidthResized(false)
        resizer.setHeightResized(false)
        resizer.resize(this)

        super.draw(context, widgetTheme)
    }
}