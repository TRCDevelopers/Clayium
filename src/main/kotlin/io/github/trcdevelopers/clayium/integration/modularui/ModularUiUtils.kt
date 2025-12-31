package io.github.trcdevelopers.clayium.integration.modularui

import com.cleanroommc.modularui.api.drawable.IKey
import com.cleanroommc.modularui.api.widget.IGuiAction
import com.cleanroommc.modularui.widgets.ProgressWidget
import io.github.trcdevelopers.clayium.api.util.Mods
import io.github.trcdevelopers.clayium.integration.jei.JeiPlugin

/**
 * Injects a "Show Recipes" button into the [ProgressWidget] if JEI is loaded.
 */
fun ProgressWidget.injectShowRecipesButton(categories: List<String>) : ProgressWidget {
    if (Mods.JustEnoughItems.isModLoaded) {
        this.addTooltipLine(IKey.lang("jei.tooltip.show.recipes"))
            .listenGuiAction(IGuiAction.MousePressed { _ ->
                if (!this.isBelowMouse) return@MousePressed false
                JeiPlugin.jeiRuntime.recipesGui.showCategories(categories)
                return@MousePressed true
            })
    }
    return this
}

/**
 * Injects a "Show Recipes" button into the [ProgressWidget] if JEI is loaded.
 */
fun ProgressWidget.injectShowRecipesButton(category: String) : ProgressWidget {
    return this.injectShowRecipesButton(listOf(category))
}
