package io.github.trcdevelopers.clayium.common.gui

import com.cleanroommc.modularui.api.ITheme
import com.cleanroommc.modularui.api.UpOrDown
import com.cleanroommc.modularui.api.drawable.IDrawable
import com.cleanroommc.modularui.api.widget.Interactable
import com.cleanroommc.modularui.theme.WidgetThemeEntry
import com.cleanroommc.modularui.widgets.ButtonWidget
import java.util.function.BooleanSupplier

class ButtonToggleable : ButtonWidget<ButtonToggleable>() {

    private var clickableSupplier: BooleanSupplier = BooleanSupplier { true }
    private var unclickableBackground: IDrawable? = null

    private val clickable get() = this.clickableSupplier.asBoolean

    override fun getCurrentBackground(theme: ITheme?, widgetTheme: WidgetThemeEntry<*>?): IDrawable? {
        if (!this.clickable) {
            return unclickableBackground ?: super.getCurrentBackground(theme, widgetTheme)
        }
        return super.getCurrentBackground(theme, widgetTheme)
    }

    fun clickableIf(supplier: BooleanSupplier): ButtonToggleable {
        this.clickableSupplier = supplier
        return this
    }

    fun unclickableBackground(background: IDrawable): ButtonToggleable {
        this.unclickableBackground = background
        return this
    }

    override fun onMousePressed(mouseButton: Int): Interactable.Result {
        return if (this.clickable) super.onMousePressed(mouseButton) else Interactable.Result.ACCEPT
    }

    override fun onMouseRelease(mouseButton: Int): Boolean {
        return if (this.clickable) super.onMouseRelease(mouseButton) else false
    }

    override fun onMouseTapped(mouseButton: Int): Interactable.Result {
        return if (this.clickable) super.onMouseTapped(mouseButton) else Interactable.Result.IGNORE
    }

    override fun onKeyPressed(typedChar: Char, keyCode: Int): Interactable.Result {
        return if (this.clickable) super.onKeyPressed(typedChar, keyCode) else Interactable.Result.IGNORE
    }

    override fun onKeyRelease(typedChar: Char, keyCode: Int): Boolean {
        return if (this.clickable) super.onKeyRelease(typedChar, keyCode) else false
    }

    override fun onKeyTapped(typedChar: Char, keyCode: Int): Interactable.Result {
        return if (this.clickable) super.onKeyTapped(typedChar, keyCode) else Interactable.Result.IGNORE
    }

    override fun onMouseScroll(scrollDirection: UpOrDown?, amount: Int): Boolean {
        return if (this.clickable) super.onMouseScroll(scrollDirection, amount) else false
    }

    override fun onMouseDrag(mouseButton: Int, timeSinceClick: Long) {
        if (this.clickable) super.onMouseDrag(mouseButton, timeSinceClick)
    }
}