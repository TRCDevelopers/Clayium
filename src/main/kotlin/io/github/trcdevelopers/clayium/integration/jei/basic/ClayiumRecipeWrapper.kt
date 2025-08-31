package io.github.trcdevelopers.clayium.integration.jei.basic

import io.github.trcdevelopers.clayium.common.recipe.Recipe
import io.github.trcdevelopers.clayium.integration.modularui.CNumFormat
import mezz.jei.api.ingredients.IIngredients
import mezz.jei.api.ingredients.VanillaTypes
import mezz.jei.api.recipe.IRecipeWrapper
import net.minecraft.client.Minecraft

open class ClayiumRecipeWrapper(
    val recipe: Recipe,
) : IRecipeWrapper {
    override fun getIngredients(ingredients: IIngredients) {
        ingredients.setInputLists(VanillaTypes.ITEM, recipe.inputs.map { it.stacks })
        ingredients.setOutputs(VanillaTypes.ITEM, recipe.outputs)
    }

    override fun drawInfo(minecraft: Minecraft, recipeWidth: Int, recipeHeight: Int, mouseX: Int, mouseY: Int) {
        val energyConsumed = CNumFormat.format((recipe.cePerTick.energy.toDouble() * recipe.duration) / 100_000)
        val craftTime = CNumFormat.format(recipe.duration.toDouble())
        minecraft.fontRenderer.drawString("Tier: ${recipe.recipeTier}", 6, 43, 0x404040)
        minecraft.fontRenderer.drawString(
            "${recipe.cePerTick.format()}/t x ${craftTime}t = ${energyConsumed}CE",
            6, 52, 0x404040
        )
    }
}