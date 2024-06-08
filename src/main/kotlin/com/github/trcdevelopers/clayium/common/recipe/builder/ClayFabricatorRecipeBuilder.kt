package com.github.trcdevelopers.clayium.common.recipe.builder

import com.github.trcdevelopers.clayium.api.item.ITieredItem
import com.github.trcdevelopers.clayium.common.blocks.ClayiumBlocks
import com.github.trcdevelopers.clayium.common.clayenergy.ClayEnergy
import com.github.trcdevelopers.clayium.common.clayenergy.IEnergizedClayItem
import com.github.trcdevelopers.clayium.common.recipe.Recipe

/**
 * Builder for creating a recipe for the (solar) clay fabricator.
 *
 * Only input, tier are required to create a recipe. Output is clay block that has `input.tierNumeric + 1`, or can be set manually.
 * If input is not tiered, the output will be clay block with tier 0.
 * Duration is calculated by [requiredTicksCalculator], or can be set manually.
 * [cePerTick] is automatically calculated.
 */
class ClayFabricatorRecipeBuilder(
    private val requiredTicksCalculator: (machineTier: Int, inputTier: Int) -> (Long),
) : RecipeBuilder<ClayFabricatorRecipeBuilder>() {

    override fun buildAndRegister() {
        val input = inputs.take(1)
        val inputTier: Int = input[0].stacks
            .filter { it.item is ITieredItem }
            .map { (it.item as ITieredItem).getTier(it) }
            .minOfOrNull { it.numeric } ?: -1
        val outputStack = ClayiumBlocks.getCompressedClayStack(inputTier + 1)
        val duration = if (duration == 0L) requiredTicksCalculator(this.tier, inputTier) else duration
        val ceProduced = (outputStack.item as? IEnergizedClayItem)?.getClayEnergy(outputStack) ?: ClayEnergy.ZERO
        val cePerTick = if (duration == 0L) ClayEnergy.ZERO else ClayEnergy(ceProduced.energy / duration)

        val recipe = Recipe(
            inputs = input,
            outputs = listOf(outputStack),
            duration = duration,
            cePerTick = cePerTick,
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