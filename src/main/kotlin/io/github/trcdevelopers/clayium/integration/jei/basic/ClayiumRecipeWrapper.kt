package io.github.trcdevelopers.clayium.integration.jei.basic

import io.github.trcdevelopers.clayium.common.recipe.Recipe
import io.github.trcdevelopers.clayium.common.util.CNumberFormat
import mezz.jei.api.ingredients.IIngredients
import mezz.jei.api.ingredients.VanillaTypes
import mezz.jei.api.recipe.IRecipeWrapper
import net.minecraft.client.Minecraft
import java.math.RoundingMode

private val NUM_FORMATTER = CNumberFormat(
    CNumberFormat.Thresholds.default,
    CNumberFormat.Units.default,
    RoundingMode.HALF_DOWN,
    "0.###"
)

open class ClayiumRecipeWrapper(
    val recipe: Recipe,
) : IRecipeWrapper {
    override fun getIngredients(ingredients: IIngredients) {
        ingredients.setInputLists(VanillaTypes.ITEM, recipe.inputs.map { it.stacks })
        ingredients.setOutputs(VanillaTypes.ITEM, recipe.outputs)
    }

    override fun drawInfo(minecraft: Minecraft, recipeWidth: Int, recipeHeight: Int, mouseX: Int, mouseY: Int) {
        val oneCe = 100_000
        val energyConsumed = NUM_FORMATTER.format((recipe.cePerTick.energy.toDouble() * recipe.duration) / oneCe)
        val craftTime = NUM_FORMATTER.format(recipe.duration.toDouble())
        minecraft.fontRenderer.drawString("Tier: ${recipe.recipeTier}", 6, 43, 0x404040)
        minecraft.fontRenderer.drawString(
            "${recipe.cePerTick.formatWith(NUM_FORMATTER)}/t x ${craftTime}t = ${energyConsumed}CE",
            6, 52, 0x404040
        )
    }
}