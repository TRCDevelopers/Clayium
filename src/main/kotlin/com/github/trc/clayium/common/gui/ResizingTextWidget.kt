package com.github.trc.clayium.common.gui

import com.cleanroommc.modularui.api.drawable.IKey
import com.cleanroommc.modularui.screen.viewport.GuiContext
import com.cleanroommc.modularui.theme.WidgetTheme
import com.cleanroommc.modularui.widgets.TextWidget

class ResizingTextWidget(key: IKey) : TextWidget(key) {
    override fun draw(context: GuiContext, widgetTheme: WidgetTheme) {
        val resizer = this.resizer()
        resizer.setWidthResized(false)
        resizer.setHeightResized(false)
        resizer.setPosResized(false, false)
        resizer.resize(this)

        super.draw(context, widgetTheme)
    }
}