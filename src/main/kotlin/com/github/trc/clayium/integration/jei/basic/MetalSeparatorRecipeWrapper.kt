package com.github.trc.clayium.integration.jei.basic

import com.github.trc.clayium.common.recipe.Recipe
import mezz.jei.api.ingredients.IIngredients
import mezz.jei.api.ingredients.VanillaTypes
import net.minecraft.client.Minecraft

class MetalSeparatorRecipeWrapper(
    data: RecipeData,
) : ClayiumRecipeWrapper(data.recipe) {
    private val index = data.index

    override fun getIngredients(ingredients: IIngredients) {
        ingredients.setInputLists(VanillaTypes.ITEM, recipe.inputs.map { it.stacks })
        ingredients.setOutput(VanillaTypes.ITEM, recipe.chancedOutputs!!.chancedOutputs[index].result)
    }

    override fun drawInfo(minecraft: Minecraft, recipeWidth: Int, recipeHeight: Int, mouseX: Int, mouseY: Int) {
        val chance = "${recipe.chancedOutputs!!.chancedOutputs[index].chance / 100.0} %"
        val chanceTextWidth = minecraft.fontRenderer.getStringWidth(chance)

        minecraft.fontRenderer.drawString(chance, (recipeWidth - chanceTextWidth) / 2, 3, 0x404040)
    }

    class RecipeData(val recipe: Recipe, val index: Int)
}