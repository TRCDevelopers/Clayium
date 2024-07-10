package com.github.trcdevelopers.clayium.common.recipe.builder

import com.github.trcdevelopers.clayium.api.capability.ClayiumCapabilities
import com.github.trcdevelopers.clayium.common.clayenergy.ClayEnergy
import com.github.trcdevelopers.clayium.common.recipe.Recipe
import com.github.trcdevelopers.clayium.common.unification.material.Material
import com.github.trcdevelopers.clayium.common.unification.material.PropertyKey
import com.github.trcdevelopers.clayium.common.unification.ore.OrePrefix

/**
 * Builder for creating a recipe for the (solar) clay fabricator.
 *
 * Output is automatically set if input material has clay property, or can be set manually.
 * Duration is calculated by [requiredTicksCalculator], or can be set manually.
 * [CEt] is automatically calculated.
 */
class ClayFabricatorRecipeBuilder : RecipeBuilder<ClayFabricatorRecipeBuilder> {

    constructor(requiredTicksCalculator: (machineTier: Int, inputTier: Int) -> (Long)) : super() {
        this.requiredTicksCalculator = requiredTicksCalculator
    }

    constructor(another: ClayFabricatorRecipeBuilder) : super(another) {
        this.requiredTicksCalculator = another.requiredTicksCalculator
    }

    private val requiredTicksCalculator: (machineTier: Int, inputTier: Int) -> (Long)

    private var inputTier = 0

    override fun buildAndRegister() {
        val duration = if (duration == 0L) requiredTicksCalculator(this.tier, inputTier) else duration

        val outputEnergy = outputs[0].getCapability(ClayiumCapabilities.ENERGIZED_CLAY, null)?.getClayEnergy()?.energy ?: 0
        val inputEnergy = inputs[0].stacks.firstNotNullOfOrNull { it.getCapability(ClayiumCapabilities.ENERGIZED_CLAY, null)?.getClayEnergy() }?.energy ?: 0
        val ceProduced = if (outputEnergy > inputEnergy) outputEnergy - inputEnergy else 0
        val cet = if (duration == 0L) ClayEnergy.ZERO else ClayEnergy(ceProduced / duration)

        val recipe = Recipe(
            inputs = this.inputs,
            outputs = this.outputs,
            duration = duration,
            cePerTick = cet,
            tierNumeric = tier
        )

        recipeRegistry.addRecipe(recipe)
    }

    override fun input(orePrefix: OrePrefix, material: Material, amount: Int): ClayFabricatorRecipeBuilder {
        super.input(orePrefix, material, amount)
        val clay = material.getPropOrNull(PropertyKey.CLAY)
        if (clay != null && clay.compressedInto != null) {
            this.output(orePrefix, clay.compressedInto)
        }
        if (material.tier != null) {
            this.inputTier = material.tier.numeric
        }
        return this
    }

    override fun copy() = ClayFabricatorRecipeBuilder(this)

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