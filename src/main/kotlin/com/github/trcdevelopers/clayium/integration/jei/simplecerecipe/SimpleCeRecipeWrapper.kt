package com.github.trcdevelopers.clayium.integration.jei.simplecerecipe

import com.github.trcdevelopers.clayium.common.recipe.SimpleCeRecipe
import com.github.trcdevelopers.clayium.common.util.UtilLocale
import mezz.jei.api.ingredients.IIngredients
import mezz.jei.api.ingredients.VanillaTypes
import mezz.jei.api.recipe.IRecipeWrapper
import net.minecraft.client.Minecraft

class SimpleCeRecipeWrapper(
    val recipe: SimpleCeRecipe,
) : IRecipeWrapper {

    val inputSize = recipe.inputSize
    val outputSize = recipe.outputSize

    override fun getIngredients(ingredients: IIngredients) {
        for (input in recipe.inputs) {
            ingredients.setInputLists(VanillaTypes.ITEM, listOf(input.inputStacks))
        }
        ingredients.setOutputs(VanillaTypes.ITEM, recipe.outputsList)
    }

    override fun drawInfo(minecraft: Minecraft, recipeWidth: Int, recipeHeight: Int, mouseX: Int, mouseY: Int) {
        minecraft.fontRenderer.drawString("Tier: ${recipe.tier}", 6, 43, 0x404040)
        minecraft.fontRenderer.drawString(
            "${recipe.cePerTick}CE/t x ${UtilLocale.craftTimeNumeral(recipe.requiredTicks.toLong())}t = ${(recipe.cePerTick * recipe.requiredTicks)}CE",
            6, 52, 0x404040
        )
        super.drawInfo(minecraft, recipeWidth, recipeHeight, mouseX, mouseY)
    }

}