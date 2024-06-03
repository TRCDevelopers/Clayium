package com.github.trcdevelopers.clayium.common.recipe.builder

import com.github.trcdevelopers.clayium.api.item.ITieredItem
import com.github.trcdevelopers.clayium.api.util.ITier
import com.github.trcdevelopers.clayium.common.clayenergy.ClayEnergy
import com.github.trcdevelopers.clayium.common.recipe.Recipe
import net.minecraft.item.ItemStack

/**
 * Builder for creating a recipe for the (solar) clay fabricator.
 *
 * Only input, tier are required to create a recipe. Output is clay block that has `input.tierNumeric + 1`.
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
            .minOf { it.numeric }
        val output = getClayBlock(inputTier + 1)


        val recipe = Recipe(
            inputs = input,
            outputs = listOf(output),
            duration = requiredTicksCalculator(this.tier, inputTier),
            cePerTick = ClayEnergy.ZERO,
            tierNumeric = tier
        )
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

    private fun getClayBlock(tierNum: Int): ItemStack {
        TODO()
    }
}