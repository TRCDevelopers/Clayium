package com.github.trc.clayium.integration.jei.basic

import com.cleanroommc.modularui.utils.NumberFormat
import com.github.trc.clayium.common.recipe.Recipe
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

    override fun drawInfo(
        minecraft: Minecraft,
        recipeWidth: Int,
        recipeHeight: Int,
        mouseX: Int,
        mouseY: Int
    ) {
        val energyConsumed =
            NumberFormat.formatWithMaxDigits(
                (recipe.cePerTick.energy.toDouble() * recipe.duration) / 100_000,
                3
            )
        val craftTime = NumberFormat.formatWithMaxDigits(recipe.duration.toDouble(), 3)
        minecraft.fontRenderer.drawString("Tier: ${recipe.recipeTier}", 6, 43, 0x404040)
        minecraft.fontRenderer.drawString(
            "${recipe.cePerTick.format()}/t x ${craftTime}t = ${energyConsumed}CE",
            6,
            52,
            0x404040
        )
    }
}
