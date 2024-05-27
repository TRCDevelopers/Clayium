package com.github.trcdevelopers.clayium.integration.jei.basic

import com.github.trcdevelopers.clayium.common.recipe.Recipe
import com.github.trcdevelopers.clayium.common.util.UtilLocale
import mezz.jei.api.ingredients.IIngredients
import mezz.jei.api.ingredients.VanillaTypes
import mezz.jei.api.recipe.IRecipeWrapper
import net.minecraft.client.Minecraft

class ClayiumRecipeWrapper(
    val recipe: Recipe,
) : IRecipeWrapper {
    override fun getIngredients(ingredients: IIngredients) {
        ingredients.setInputLists(VanillaTypes.ITEM, recipe.inputs.map { it.stacks })
        ingredients.setOutputs(VanillaTypes.ITEM, recipe.outputs)
    }

    override fun drawInfo(minecraft: Minecraft, recipeWidth: Int, recipeHeight: Int, mouseX: Int, mouseY: Int) {
        minecraft.fontRenderer.drawString("Tier: ${recipe.tierNumeric}", 6, 43, 0x404040)
        minecraft.fontRenderer.drawString(
            "${recipe.cePerTick}CE/t x ${UtilLocale.craftTimeNumeral(recipe.duration.toLong())}t = ${(recipe.cePerTick * recipe.duration)}CE",
            6, 52, 0x404040
        )
        super.drawInfo(minecraft, recipeWidth, recipeHeight, mouseX, mouseY)
    }
}