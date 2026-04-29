package io.github.trcdevelopers.clayium.integration.modularui

import com.cleanroommc.modularui.integration.recipeviewer.RecipeViewerRecipeTransferHandler
import com.cleanroommc.modularui.screen.ModularPanel
import com.cleanroommc.modularui.screen.ModularScreen
import io.github.trcdevelopers.clayium.api.MOD_ID
import mezz.jei.api.gui.IRecipeLayout
import mezz.jei.api.recipe.transfer.IRecipeTransferError
import mezz.jei.transfer.RecipeTransferErrorTooltip

@Suppress("UnstableApiUsage")
class ModularScreenClayium(
    panel: ModularPanel
) : ModularScreen(MOD_ID, panel), RecipeViewerRecipeTransferHandler {
    init {
        this.useTheme(ModularUiInit.CLAYIUM_DEFAULT_THEME)
    }

    override fun transferRecipe(recipeLayout: IRecipeLayout, maxTransfer: Boolean, simulate: Boolean): IRecipeTransferError {
        return RecipeTransferErrorTooltip("This feature is WIP for Clayium")
    }
}