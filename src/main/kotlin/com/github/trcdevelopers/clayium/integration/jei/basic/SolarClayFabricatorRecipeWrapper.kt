package com.github.trcdevelopers.clayium.integration.jei.basic

import com.github.trcdevelopers.clayium.common.recipe.Recipe
import net.minecraft.client.Minecraft
import net.minecraft.client.resources.I18n

class SolarClayFabricatorRecipeWrapper(
    recipe: Recipe,
) : ClayiumRecipeWrapper(recipe) {
    override fun drawInfo(minecraft: Minecraft, recipeWidth: Int, recipeHeight: Int, mouseX: Int, mouseY: Int) {
        // draw "Tier" at center top
        val tierText = "Tier: ${recipe.tierNumeric}"
        val tierTextWidth = minecraft.fontRenderer.getStringWidth(tierText)
        minecraft.fontRenderer.drawString(tierText, (recipeWidth - tierTextWidth) / 2, 3, 0x404040)


        minecraft.fontRenderer.drawString(
            "${recipe.duration}t = ${I18n.format("recipe.clayium.second", (recipe.duration / 20))} (${I18n.format("tooltip.clayium.ce", recipe.cePerTick)}/t)",
            6, 50, 0x404040
        )
    }
}