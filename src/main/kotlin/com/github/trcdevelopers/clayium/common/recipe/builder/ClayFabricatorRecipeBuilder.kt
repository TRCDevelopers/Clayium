package com.github.trcdevelopers.clayium.common.recipe.builder

import com.github.trcdevelopers.clayium.api.item.ITieredItem
import com.github.trcdevelopers.clayium.api.util.ITier
import com.github.trcdevelopers.clayium.common.blocks.ClayiumBlocks
import com.github.trcdevelopers.clayium.common.clayenergy.ClayEnergy
import com.github.trcdevelopers.clayium.common.recipe.Recipe
import net.minecraft.item.ItemStack

/**
 * Builder for creating a recipe for the (solar) clay fabricator.
 *
 * Only input, tier are required to create a recipe. Output is clay block that has `input.tierNumeric + 1`.
 * Duration is calculated by [requiredTicksCalculator], or can be set manually.
 * Calling [output] does not add the output to the recipe.
 */
class ClayFabricatorRecipeBuilder(
    private val requiredTicksCalculator: (machineTier: Int, inputTier: Int) -> (Long),
) : RecipeBuilder<ClayFabricatorRecipeBuilder>() {

    override fun buildAndRegister() {
        val input = inputs.take(1)
        val inputTier: Int = input[0].stacks
            .filter { it.item is ITieredItem }
            .map { (it.item as ITieredItem).getTier(it) }
            .minOfOrNull { it.numeric } ?: 0
        val output = ClayiumBlocks.getCompressedClayStack(inputTier + 1)

        val recipe = Recipe(
            inputs = input,
            outputs = listOf(output),
            duration = if (duration <= 0) requiredTicksCalculator(this.tier, inputTier) else duration,
            cePerTick = ClayEnergy.ZERO,
            tierNumeric = tier
        )

        recipeRegistry.addRecipe(recipe)
    }

    override fun copy(): ClayFabricatorRecipeBuilder {
        return ClayFabricatorRecipeBuilder(requiredTicksCalculator)
            .setRegistry(this.recipeRegistry)
            .inputs(*inputs.toTypedArray())
            .outputs(*outputs.toTypedArray())
            .duration(duration)
            .cePerTick(cePerTick)
            .tier(tier)
    }

    companion object {
        fun solarClayFabricator(machineTier: Int, inputTier: Int): Long {
            val a = when (machineTier) {
                5 -> 4.0
                6 -> 6.0
                7 -> 9.0
                else -> 1.0
            }

            val b = when (machineTier) {
                5 -> 4.0
                6 -> 3.0
                7 -> 2.0
                else -> 1.0
            }

            val multi = when (machineTier) {
                5 -> 250
                6 -> 2500
                7 -> 150000
                else -> 1
            }

            val n = (Math.pow(10.0, a + 1.0) * (b - 1)) / (Math.pow(b, a)  - 1)

            return (Math.pow(b, inputTier.toDouble()) * (n / multi)).toLong()
        }
    }
}