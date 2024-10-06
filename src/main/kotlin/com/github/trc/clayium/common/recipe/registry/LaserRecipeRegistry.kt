package com.github.trc.clayium.common.recipe.registry

import com.github.trc.clayium.common.recipe.LaserRecipe
import com.github.trc.clayium.common.recipe.builder.LaserRecipeBuilder
import net.minecraft.block.Block
import net.minecraft.block.state.IBlockState

class LaserRecipeRegistry {
    private val _recipes = mutableListOf<LaserRecipe>()
    val recipes get() = _recipes.toList()

    private fun register0(input: Block, inputMeta: Int?, output: Block, outputMeta: Int?, energyMin: Double, energyMax: Double?, requiredEnergy: Double) {
        _recipes.add(LaserRecipe(input, output, inputMeta, outputMeta, energyMin, energyMax?: Double.MAX_VALUE, requiredEnergy))
        _recipes.sortWith(RECIPE_COMPARATOR)
    }

    fun register(input: Block, inputMeta: Int, output: Block, outputMeta: Int, energyMin: Double, energyMax: Double, requiredEnergy: Double) = register0(input, inputMeta, output, outputMeta, energyMin, energyMax, requiredEnergy)
    fun register(input: Block, output: Block, energyMin: Double, requiredEnergy: Double) = register0(input, null, output, null, energyMin, null, requiredEnergy)
    fun register(input: Block, output: Block, energyMin: Double, energyMax: Double, requiredEnergy: Double) = register0(input, null, output, null, energyMin, energyMax, requiredEnergy)
    fun register(input: Block, inputMeta: Int, output: Block, energyMin: Double, energyMax: Double, requiredEnergy: Double) = register0(input, inputMeta, output, null, energyMin, energyMax, requiredEnergy)
    fun register(input: Block, output: Block, outputMeta: Int, energyMin: Double, energyMax: Double, requiredEnergy: Double) = register0(input, null, output, outputMeta, energyMin, energyMax, requiredEnergy)
    fun register(input: Block, inputMeta: Int, output: Block, energyMin: Double, requiredEnergy: Double) = register0(input, inputMeta, output, null, energyMin, null, requiredEnergy)
    fun register(input: Block, output: Block, outputMeta: Int, energyMin: Double, requiredEnergy: Double) = register0(input, null, output, outputMeta, energyMin, null, requiredEnergy)

    fun register(create: LaserRecipeBuilder.() -> Unit) {
        val builder = LaserRecipeBuilder()
        builder.create()
        _recipes.add(builder.build())
        _recipes.sortWith(RECIPE_COMPARATOR)
    }
    fun getRecipe(input: IBlockState, energy: Double): LaserRecipe? {
        for (recipe in _recipes) {
            if (recipe.matches(input, energy)) {
                return recipe
            }
        }
        return null
    }

    companion object {
        private val RECIPE_COMPARATOR = compareBy<LaserRecipe> {
            it.energyMin
        }.reversed()
    }
}