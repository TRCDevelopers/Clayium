package com.github.trc.clayium.common.gui

import com.cleanroommc.modularui.api.drawable.IKey
import com.cleanroommc.modularui.screen.viewport.ModularGuiContext
import com.cleanroommc.modularui.theme.WidgetTheme
import com.cleanroommc.modularui.widgets.TextWidget

class ResizingTextWidget(key: IKey) : TextWidget(key) {
    override fun draw(context: ModularGuiContext, widgetTheme: WidgetTheme) {
        val resizer = this.resizer()
        resizer.setWidthResized(false)
        resizer.setHeightResized(false)
        resizer.resize(this)

        super.draw(context, widgetTheme)
    }
}